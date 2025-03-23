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

  @override
  void initState() {
    super.initState();
    rehearsals = getPropositions(context);
  }

  Future<Map<String, Map<String, List<dynamic>>>> getPropositions(
      BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/calendarCP';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> popositions = data.map((item) {
          return {
            'rehearsalId': item['rehearsalId'],
            'accepted': item['accepted'],
            'beginningDate': item['beginningDate'],
          };
        }).toList();
        Map<String, Map<String, List<dynamic>>> rehearsalsByMonth = {};
        for (var rehearsal in popositions) {
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
          Utils.errorMess('Erreur lors de la récupérations de la proposition',
              'Une erreur c\'est produite', context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de la récupérations de la proposition',
            'Une erreur c\'est produite', context);
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

  String getDayName(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    const days = ["Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"];
    return days[date.weekday % 7];
  }

  String formatDate(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${date.day} ${getMonthName(date.month)} ${date.year}';
  }

  String getMonthYear(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${getMonthName(date.month)} ${date.year}';
  }

  String getDay(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${date.day}';
  }

  String formatTime(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return "${date.hour}:${date.minute}";
  }

  void accept(int id){
    //TODO
  }

  //Done with the help of chatgpt
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
                FutureBuilder<Map<String, Map<String, List<dynamic>>>>(
                  future: rehearsals,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return const CircularProgressIndicator();
                    } else if (snapshot.hasError) {
                      return const Text('Erreur de chargement');
                    } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                      return const Text('Aucune proposition');
                    }

                    return Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: snapshot.data!.entries.map((entry) {
                        String monthYear = entry.key;
                        Map<String, List<dynamic>> daysMap = entry.value;

                        return Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Padding(
                              padding:
                                  const EdgeInsets.symmetric(vertical: 8.0),
                              child: Text(
                                monthYear,
                                style: const TextStyle(
                                    fontSize: 20, fontWeight: FontWeight.bold),
                              ),
                            ),
                            ...daysMap.entries.map((dayEntry) {
                              String day = dayEntry.key;
                              List<dynamic> reherasalsList = dayEntry.value;

                              // Sort events by beginningDate
                              reherasalsList.sort((a, b) =>
                                  DateTime.parse(a['beginningDate']).compareTo(
                                      DateTime.parse(b['beginningDate'])));

                              return Padding(
                                padding:
                                    const EdgeInsets.symmetric(vertical: 6.0),
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Column(
                                      children: [
                                        Container(
                                          width: 50,
                                          alignment: Alignment.center,
                                          margin:
                                              const EdgeInsets.only(top: 12),
                                          child: Column(
                                            children: [
                                              Text(
                                                getDayName(reherasalsList
                                                    .first['beginningDate']),
                                                style: const TextStyle(
                                                    fontSize: 15,
                                                    fontWeight:
                                                        FontWeight.bold),
                                              ),
                                              Text(
                                                day,
                                                style: const TextStyle(
                                                    fontSize: 19,
                                                    fontWeight:
                                                        FontWeight.bold),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(width: 12),

                                    // List of rehearsals for the day
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        children: reherasalsList.map((rehearsal) {
                                          return Container(
                                            width: 500,
                                            margin: const EdgeInsets.only(
                                                bottom: 8),
                                            padding: const EdgeInsets.all(12),
                                            decoration: BoxDecoration(
                                              color: rehearsal['accepted']
                                                  ? Colors.green[200]
                                                  : const Color.fromARGB(
                                                      255, 254, 179, 198),
                                              borderRadius:
                                                  BorderRadius.circular(12),
                                            ),
                                            child: Row(
                                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                              children: [
                                                Column(
                                                  crossAxisAlignment:
                                                      CrossAxisAlignment.start,
                                                  children: [
                                                    //TODO actualy get the rehearsal name and duration and make the widget clikable to see the detail of the rehearsal
                                                    Text(
                                                      "Répétition ${rehearsal['rehearsalId']}",
                                                      style: const TextStyle(
                                                          fontSize: 18),
                                                    ),
                                                    Text(
                                                      "${formatTime(rehearsal['beginningDate'])} - ${formatTime(DateTime.parse(rehearsal['beginningDate']).add(const Duration(hours: 1)).toString())}",
                                                      style: const TextStyle(
                                                          fontSize: 14,
                                                          fontStyle:
                                                              FontStyle.italic),
                                                    ),
                                                  ],
                                                ),
                                                GestureDetector(
                                                  onTap: () {
                                                    accept(rehearsal['rehearsalId']);
                                                  },
                                                  child: Icon(
                                                    rehearsal['accepted'] ? Icons.close : Icons.check,
                                                    size: 30,
                                                  ),
                                                ),
                                              ],
                                            ),
                                          );
                                        }).toList(),
                                      ),
                                    ),
                                  ],
                                ),
                              );
                            }),
                          ],
                        );
                      }).toList(),
                    );
                  },
                ),
              ],
            ),
          ),
        ),
      ),
      selectedIndex: 1,
    );
  }
}
