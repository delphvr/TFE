import 'package:calendar_app/calendar/calendar_list.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// For the project with id [projectId] ask the backend for the calendar proposotion and display it.
/// With a button to validate or recompute the rehearsals not accepted yet.
class CalendarPropositionPage extends StatefulWidget {
  final int projectId;

  const CalendarPropositionPage({
    super.key,
    required this.projectId,
  });

  @override
  State<CalendarPropositionPage> createState() =>
      _CalendarPropositionPageState();
}

class _CalendarPropositionPageState extends State<CalendarPropositionPage> {
  final user = FirebaseAuth.instance.currentUser!;
  final String errorTitle = 'Erreur lors de la récupération de la proposition';
  late Future<Map<String, Map<String, List<dynamic>>>>? rehearsals;
  Future<Map<int, Map<int, bool>>>? participations = Future.value({});

  @override
  void initState() {
    super.initState();
    fetchData(false);
  }

  /// Update the variables [rehearsals] and [participations] with the data retreived from the backend for the calendar proposition.
  /// [rehearsals] the proposed rehearsals time slots, [participations] who is available for which rehearsal.
  /// If [recompute] is true, the server will recalculate a new proposition, keeping the already accepted time slots.
  void fetchData(bool recompute) async {
    setState(() {
      rehearsals = getPropositions(context, recompute);
    });
    await rehearsals; //If proposition not computed won't get the participations
    setState(() {
      participations = getParticipation(context);
    });
  }

  /// Get informtion about the rehearsal with id [id], from the backend.
  /// If an error occurs an error message will be displayed.
  dynamic getRehearsal(int id) async {
    final String url = '${dotenv.env['API_BASE_URL']}/rehearsals/$id';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        return json.decode(utf8.decode(response.bodyBytes));
      } else {
        if (mounted) {
          Utils.errorMess(errorTitle,
              'Une erreur c\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(errorTitle,
            'Une erreur c\'est produite', context);
      }
      return {};
    }
  }

  /// Get the proposal planning from the backend. The rehearsals will be sorted by dates.
  /// If an error occurs an error message will be display.
  /// [Return] a map organized by [monthYear] -> [day] -> [list of rehearsals].
  Future<Map<String, Map<String, List<dynamic>>>> getPropositions(
      BuildContext context, bool recompute) async {
    String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/calendarCP';
    if (recompute) {
      url += '?recompute=true';
    }
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> propositions =
            await Future.wait(data.map((item) async {
          final rehearsalData = await getRehearsal(item['rehearsalId']);
          return {
            'rehearsalId': item['rehearsalId'],
            'accepted': item['accepted'],
            'beginningDate': item['beginningDate'],
            'name': rehearsalData['name'],
            'date': rehearsalData['date'],
            'time': rehearsalData['time'],
            'duration': rehearsalData['duration'],
          };
        }));

        propositions.sort((a, b) {
          DateTime dateA = DateTime.parse(a['beginningDate']);
          DateTime dateB = DateTime.parse(b['beginningDate']);
          int yearComparison = dateA.year.compareTo(dateB.year);
          if (yearComparison != 0) return yearComparison;
          int monthComparison = dateA.month.compareTo(dateB.month);
          if (monthComparison != 0) return monthComparison;
          return dateA.day.compareTo(dateB.day);
        });

        Map<String, Map<String, List<dynamic>>> rehearsalsByMonth = {};
        for (var rehearsal in propositions) {
          String monthYear = getMonthYear(rehearsal['beginningDate']);
          String day = getDay(rehearsal['beginningDate']);
          if (!rehearsalsByMonth.containsKey(monthYear)) {
            rehearsalsByMonth[monthYear] = {};
          }
          if (!rehearsalsByMonth[monthYear]!.containsKey(day)) {
            rehearsalsByMonth[monthYear]![day] = [];
          }
          rehearsalsByMonth[monthYear]?[day]?.add(rehearsal);
        }
        return rehearsalsByMonth;
      } else if(response.statusCode == 404){
          if (context.mounted) {
          Utils.errorMess(errorTitle,
              'Aucune solution trouvée, horaire infaisable.', context);
        }
        return {};
      } else {
        if (context.mounted) {
          Utils.errorMess(errorTitle,
              'Une erreur s\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess(errorTitle,
            'Une erreur s\'est produite', context);
      }
      return {};
    }
  }

  /// Get for each rehearsal how are the users that are present and who are not.
  /// If an error occurs an error message will be display.
  /// [Return] a map [rehearsalId] -> [userId] -> [bool available].
  Future<Map<int, Map<int, bool>>> getParticipation(
      BuildContext context) async {
    String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/CPpresences';

    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final Map<String, dynamic> rawData =
            json.decode(utf8.decode(response.bodyBytes));

        // Convert keys from String to int
        Map<int, Map<int, bool>> parsedData = rawData.map((key, value) {
          return MapEntry(
            int.parse(key), 
            (value as Map<String, dynamic>).map((subKey, subValue) {
              return MapEntry(int.parse(subKey),
                  subValue as bool); 
            }),
          );
        });

        return parsedData;
      } else {
        if (context.mounted) {
          Utils.errorMess(errorTitle,
              'Une erreur s\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess(errorTitle,
            'Une erreur s\'est produite', context);
      }
      return {};
    }
  }

  /// Returns the name of the month given its [month] number.
  String getMonthName(int month) {
    const months = [
      "Janvier",
      "Février",
      "Mars",
      "Avril",
      "Mai",
      "Jun",
      "Juillet",
      "Août",
      "Septembre",
      "Octobre",
      "Novembre",
      "Decembre"
    ];
    return months[month - 1];
  }

  /// Get the month year from a [dateTime] string.
  String getMonthYear(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${getMonthName(date.month)} ${date.year}';
  }

  /// Get and return the day of the month from a [dateTime] string.
  String getDay(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${date.day}';
  }

  /// Notify the backend that the accptance state of the rehearsal [rehearsal] has changed.
  /// If an error occurs an error message will be display.
  void accept(Map rehearsal) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/rehearsals/${rehearsal['rehearsalId']}/accepted?accepted=${!rehearsal['accepted']}';
    try {
      final response = await http.patch(Uri.parse(url));

      if (response.statusCode == 200) {
        setState(() {
          rehearsal['accepted'] = !rehearsal['accepted'];
        });
      } else {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plus tard', context);
      }
    }
  }

  /// Accepte the calendar proposition made.
  /// If an error occurs, an error message will be displayed.
  void acceptAll() async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/calendarCP/accept';
    try {
      final response = await http.put(Uri.parse(url));
      if (response.statusCode == 200) {
        if (mounted) {
          Navigator.of(context).pop();
        }
      } else {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plus tard', context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Align(
        alignment: Alignment.topCenter,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 35),
          child: SingleChildScrollView(
            child: Column(
              children: [
                const SizedBox(height: 25),
                const Text(
                  'Proposition de calendrier',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 26,
                  ),
                ),
                const SizedBox(height: 25),
                CalendarList(
                  projectId: widget.projectId,
                  rehearsals: rehearsals,
                  accept: accept,
                  isCalendar: false,
                  participations: participations,
                ),
                const SizedBox(height: 25),
                ButtonCustom(
                  text: 'Tout valider',
                  onTap: () {
                    acceptAll();
                  },
                ),
                const SizedBox(height: 25),
                ButtonCustom(
                  text: 'Recalculer',
                  onTap: () {
                    fetchData(true);
                  },
                ),
                const SizedBox(height: 25),
              ],
            ),
          ),
        ),
      ),
      selectedIndex: 1,
    );
  }
}
