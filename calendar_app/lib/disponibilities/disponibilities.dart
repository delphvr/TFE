import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/disponibilities/new_disponibilities.dart';
import 'package:calendar_app/disponibilities/vacation_input.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class DisponibilitiesPage extends StatefulWidget {
  const DisponibilitiesPage({
    super.key,
  });

  @override
  State<DisponibilitiesPage> createState() => _DisponibilitiesPageSate();
}

class _DisponibilitiesPageSate extends State<DisponibilitiesPage> {
  final user = FirebaseAuth.instance.currentUser!;
  late Future<Map<String, List<dynamic>>>? weeklyAvailabilities;
  late Future<List<dynamic>>? vacations;

  @override
  void initState() {
    super.initState();
    weeklyAvailabilities = getUserWeeklyAvailabilities(context);
    vacations = getUserVacations(context);
  }

  Future<Map<String, List<dynamic>>> getUserWeeklyAvailabilities(
      BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/availabilities?email=${user.email!}';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> disponibilities = data.map((item) {
          return {
            'userId': item['userId'],
            'startTime': item['startTime'],
            'endTime': item['endTime'],
            'weekday': item['weekday'],
          };
        }).toList();

        Map<String, List<dynamic>> availabilitiesByDay = {};
        for (var disponibility in disponibilities) {
          Map<int, String> weekdays = {
            0: "Lundi",
            1: "Mardi",
            2: "Mercredi",
            3: "Jeudi",
            4: "Vendredi",
            5: "Samedi",
            6: "Dimanche"
          };
          String weekday = weekdays[disponibility['weekday']]!;
          if (!availabilitiesByDay.containsKey(weekday)) {
            availabilitiesByDay[weekday] = [];
          }
          availabilitiesByDay[weekday]?.add(disponibility);
        }
        List<String> weekdaysOrder = [
          "Lundi",
          "Mardi",
          "Mercredi",
          "Jeudi",
          "Vendredi",
          "Samedi",
          "Dimanche"
        ];

        Map<String, List<dynamic>> sortedAvailabilitiesByDay = {
          for (String day in weekdaysOrder)
            if (availabilitiesByDay.containsKey(day))
              day: (availabilitiesByDay[day]!
                ..sort((a, b) => a['startTime'].compareTo(b['startTime'])))
        };

        return sortedAvailabilitiesByDay;
      } else {
        if (context.mounted) {
          Utils.errorMess(
              'Erreur lors de la récupération des disponibilités de la semaine',
              'Une erreur s\'est produite',
              context);
        }
        return {};
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess(
            'Erreur lors de la récupération des disponibilités de la semaine',
            'Une erreur s\'est produite',
            context);
      }
      return {};
    }
  }

  Future<List<dynamic>> getUserVacations(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/vacations?email=${user.email!}';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> vacations = data.map((item) {
          return {
            'userId': item['userId'],
            'startDate': item['startDate'],
            'endDate': item['endDate'],
          };
        }).toList();

        return vacations;
      } else {
        if (context.mounted) {
          Utils.errorMess('Erreur lors de la récupération des vacances',
              'Une erreur s\'est produite', context);
        }
        return [];
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de la récupération des vacances',
            'Une erreur s\'est produite', context);
      }
      return [];
    }
  }

  void deleteDisponibility(disponibility) async {
    try {
      final String url = '${dotenv.env['API_BASE_URL']}/availabilities';
      final response = await http.delete(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode(disponibility),
      );

      if (response.statusCode == 204) {
        setState(() {
          weeklyAvailabilities = getUserWeeklyAvailabilities(context);
        });
      } else {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard.', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
    }
  }

  void deleteVacations(vacation) async {
    try {
      final String url = '${dotenv.env['API_BASE_URL']}/vacations';
      final response = await http.delete(
        Uri.parse(url),
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode(vacation),
      );

      if (response.statusCode == 204) {
        setState(() {
          vacations = getUserVacations(context);
        });
      } else {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard.', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 35),
        child: SingleChildScrollView(
          child: Align(
            alignment: Alignment.topCenter,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const Text(
                  'Semaine type',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 26,
                  ),
                ),
                const SizedBox(height: 25),
                FutureBuilder<Map<String, List<dynamic>>>(
                  future: weeklyAvailabilities,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return const CircularProgressIndicator();
                    } else if (snapshot.hasError) {
                      return const Text('Erreur de chargement');
                    } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                      return const Text('Aucune disponibilité encodée');
                    }

                    return Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: snapshot.data!.entries.map((entry) {
                        String weekDay = entry.key;
                        List<dynamic> disponibilities = entry.value;

                        return Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Padding(
                              padding:
                                  const EdgeInsets.symmetric(vertical: 8.0),
                              child: Text(
                                weekDay,
                                style: const TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                            Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children:
                                      disponibilities.map((disponibility) {
                                    return Container(
                                      width: 200,
                                      margin: const EdgeInsets.only(bottom: 8),
                                      padding: const EdgeInsets.all(12),
                                      decoration: BoxDecoration(
                                        color: const Color(0xFFF2F2F2),
                                        borderRadius: BorderRadius.circular(8),
                                        border: Border.all(),
                                      ),
                                      child: Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.spaceBetween,
                                        children: [
                                          Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            children: [
                                              Text(
                                                "${Utils.formatTimeString(disponibility['startTime'])} - ${Utils.formatTimeString(disponibility['endTime'])}",
                                                style: const TextStyle(
                                                  fontSize: 16,
                                                ),
                                                textAlign: TextAlign.center,
                                              ),
                                            ],
                                          ),
                                          GestureDetector(
                                            onTap: () {
                                              deleteDisponibility(disponibility);
                                            },
                                            child: const Icon(Icons.close,
                                                size: 25),
                                          )
                                        ],
                                      ),
                                    );
                                  }).toList(),
                                ),
                              ],
                            ),
                          ],
                        );
                      }).toList(),
                    );
                  },
                ),
                const SizedBox(height: 25),
                ButtonCustom(
                  text: "Ajouter une disponibilité",
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) =>
                              NewDisponibilitiesPage(onpop: () {
                                setState(() {
                                  weeklyAvailabilities =
                                      getUserWeeklyAvailabilities(context);
                                });
                              })),
                    );
                  },
                ),
                const SizedBox(height: 25),
                const Text(
                  'Mes vacances',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 26,
                  ),
                ),
                const SizedBox(height: 25),
                FutureBuilder<List<dynamic>>(
                  future: vacations,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.waiting) {
                      return const CircularProgressIndicator();
                    } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                      return const Text('Aucune vacance encodée');
                    }

                    return Column(
                      children: snapshot.data!.map((vacation) {
                        return Container(
                          width: double.infinity,
                          margin: const EdgeInsets.only(bottom: 8),
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: const Color(0xFFF2F2F2),
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(),
                          ),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(
                                "Du ${Utils.formatDateString(vacation['startDate'])} au ${Utils.formatDateString(vacation['endDate'])}",
                                style: const TextStyle(fontSize: 16),
                              ),
                              GestureDetector(
                                onTap: () {
                                  deleteVacations(vacation);
                                },
                                child: const Icon(Icons.close, size: 25),
                              ),
                            ],
                          ),
                        );
                      }).toList(),
                    );
                  },
                ),
                const SizedBox(height: 25),
                ButtonCustom(
                  text: "Ajouter des vacances",
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => NewVacationsPage(onpop: () {
                                setState(() {
                                  vacations = getUserVacations(context);
                                });
                              })),
                    );
                  },
                ),
                const SizedBox(height: 25),
              ],
            ),
          ),
        ),
      ),
      selectedIndex: 3,
    );
  }
}
