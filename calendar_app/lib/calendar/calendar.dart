import 'package:calendar_app/calendar/calendar_list.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Display the rehearsal of the user that has a date and time.
/// Display a V if the user is available for that rehearsal and X if not.
class CalendarPage extends StatefulWidget {
  const CalendarPage({
    super.key,
  });

  @override
  State<CalendarPage> createState() => _CalendarPageState();
}

class _CalendarPageState extends State<CalendarPage> {
  final user = FirebaseAuth.instance.currentUser!;
  late Future<Map<String, Map<String, List<dynamic>>>>? rehearsals;
  late Future<Map<int, bool>>? userPresences = Future.value({});

  @override
  void initState() {
    super.initState();
    rehearsals = getUserRehearsals(context);
    userPresences = getUserpresences(context);
  }

  /// Get all the rehearsal the user is part of. Only returns the ones that has a date and time set.
  /// If an error occurs an error message will be display.
  Future<Map<String, Map<String, List<dynamic>>>> getUserRehearsals(
      BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/${user.email!}/rehearsals';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> propositions = data.map((item) {
          return {
            'rehearsalId': item['id'],
            'name': item['name'],
            'date': item['date'],
            'time': item['time'],
            'duration': item['duration'],
            'projectId': item['projectId'],
          };
        }).toList();

        Map<String, Map<String, List<dynamic>>> rehearsalsByMonth = {};
        for (var rehearsal in propositions) {
          if (rehearsal['date'] != null && rehearsal['time'] != null) {
            String monthYear = getMonthYear(rehearsal['date']);
            String day = getDay(rehearsal['date']);
            if (!rehearsalsByMonth.containsKey(monthYear)) {
              rehearsalsByMonth[monthYear] = {};
            }
            if (!rehearsalsByMonth[monthYear]!.containsKey(day)) {
              rehearsalsByMonth[monthYear]![day] = [];
            }
            rehearsalsByMonth[monthYear]?[day]?.add(rehearsal);
          }
        }
        return rehearsalsByMonth;
      } else {
        if (context.mounted) {
          Utils.errorMess('Erreur lors de la récupération du calendrier',
              'Une erreur s\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de la récupération du calendrier',
            'Une erreur s\'est produite', context);
      }
      return {};
    }
  }

  /// Get for which rehearsal the user is present.
  /// If an error occurs will display an error message.
  Future<Map<int, bool>> getUserpresences(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/${user.email!}/presences';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));

        final Map<int, bool> presenceMap = {};
        for (var item in data) {
          final int rehearsalId = item['rehearsalId'];
          final bool present = item['present'];
          presenceMap[rehearsalId] = present;
        }
        return presenceMap;
      } else {
        if (context.mounted) {
          Utils.errorMess('Erreur lors de la récupération des présences',
              'Une erreur s\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de la récupération des présences',
            'Une erreur s\'est produite', context);
      }
      return {};
    }
  }

  /// Send to the backend that the user presence for the rehearsal with id rehearsalId has changed.
  /// /// If an error occurs will display an error message.
  Future<Map<int, bool>> updateUserpresences(
      BuildContext context, int rehearsalId, bool presence) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/$rehearsalId/users/${user.email!}/presences?presence=$presence';
    try {
      final response = await http.put(Uri.parse(url));

      if (response.statusCode == 200) {
        final data = json.decode(utf8.decode(response.bodyBytes));
        final bool present = data['present'];

        final currentPresences = await userPresences!;

        setState(() {
          userPresences = Future.value({
            ...currentPresences,
            rehearsalId: present,
          });
        });

        return {rehearsalId: present};
      } else {
        if (context.mounted) {
          Utils.errorMess(
              'Erreur est survenue', 'Merci de réessayer plus tard', context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess(
            'Erreur est survenue', 'Merci de réessayer plus tard', context);
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
                  'Mon calendrier',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 26,
                  ),
                ),
                const SizedBox(height: 25),
                CalendarList(
                  rehearsals: rehearsals,
                  isCalendar: true,
                  userPresences: userPresences,
                  updatePresences: updateUserpresences,
                ),
                const SizedBox(height: 25),
              ],
            ),
          ),
        ),
      ),
      selectedIndex: 2,
    );
  }
}
