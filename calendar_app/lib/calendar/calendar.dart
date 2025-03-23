import 'package:calendar_app/calendar/calendar_list.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

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

  @override
  void initState() {
    super.initState();
    rehearsals = getUserRehearsals(context);
  }

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
