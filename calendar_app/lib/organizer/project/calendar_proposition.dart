import 'package:calendar_app/calendar/calendar_list.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

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
  late Future<Map<String, Map<String, List<dynamic>>>>? rehearsals;
  Future<Map<int, Map<int, bool>>>? participations = Future.value({});

  @override
  void initState() {
    super.initState();
    fetchData();
  }

  void fetchData() async {
    setState(() {
      rehearsals = getPropositions(context, false);
    });
    await rehearsals; //If proposition not computed won't get the participations
    setState(() {
      participations = getParticipation(context);
    });
  }

  dynamic getRehearsal(int id) async {
    final String url = '${dotenv.env['API_BASE_URL']}/rehearsals/$id';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        return json.decode(utf8.decode(response.bodyBytes));
      } else {
        if (mounted) {
          Utils.errorMess('Erreur lors de la récupérations de l\'agenda',
              'Une erreur c\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Erreur lors de la récupérations de l\'agenda',
            'Une erreur c\'est produite', context);
      }
      return {};
    }
  }

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
      } else {
        if (context.mounted) {
          Utils.errorMess('Erreur lors de la récupération de la proposition',
              'Une erreur s\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de la récupération de la proposition',
            'Une erreur s\'est produite', context);
      }
      return {};
    }
  }

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
            int.parse(key), // Convert first-level key to int
            (value as Map<String, dynamic>).map((subKey, subValue) {
              return MapEntry(int.parse(subKey),
                  subValue as bool); // Convert second-level key to int
            }),
          );
        });

        return parsedData;
      } else {
        print(response.statusCode);
        print(response.body);
        if (context.mounted) {
          Utils.errorMess('Erreur lors de la récupération de la proposition',
              'Une erreur s\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      print(e);
      if (context.mounted) {
        Utils.errorMess('Erreur lors de la récupération de la proposition',
            'Une erreur s\'est produite', context);
      }
      return {};
    }
  }

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

  String getMonthYear(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${getMonthName(date.month)} ${date.year}';
  }

  String getDay(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${date.day}';
  }

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
              'Merci de réessayer plustard', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plustard', context);
      }
    }
  }

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
              'Merci de réessayer plustard', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess(
            'Une erreur est survenue', 'Merci de réessayer plustard', context);
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
                    fetchData();
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
