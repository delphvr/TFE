import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

class CalendarList extends StatefulWidget {
  final int? projectId;
  final Function? accept;
  final Future<Map<String, Map<String, List<dynamic>>>>? rehearsals;
  final bool isCalendar;

  const CalendarList({
    super.key,
    this.projectId,
    required this.rehearsals,
    this.accept,
    required this.isCalendar,
  });

  @override
  State<CalendarList> createState() => _CalendarListState();
}

class _CalendarListState extends State<CalendarList> {
  final user = FirebaseAuth.instance.currentUser!;

  final colors = [
    Colors.purple[100],
    Colors.green[100],
    Colors.red[100],
    Colors.blue[100],
    Colors.pink[100],
    Colors.grey[100]
  ];

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

  String getDay(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    return '${date.day}';
  }

  String formatTime(String dateTime) {
    DateTime date = DateTime.parse(dateTime);
    String hour = date.hour.toString().padLeft(2, '0');
    String minute = date.minute.toString().padLeft(2, '0');
    return "$hour:$minute";
  }

  String getEndTime(String time, String durationstr) {
    Duration duration = Utils.parseDuration(durationstr);
    List<String> parts = time.split(':');
    Duration timeDuration =
        Duration(hours: int.parse(parts[0]), minutes: int.parse(parts[1]));
    Duration endDuration = timeDuration + duration;
    int endHours = endDuration.inHours % 24;
    int endMinutes = endDuration.inMinutes % 60;

    return '${endHours.toString().padLeft(2, '0')}:${endMinutes.toString().padLeft(2, '0')}';
  }

  //Done with the help of chatgpt
  @override
  Widget build(BuildContext context) {
    return FutureBuilder<Map<String, Map<String, List<dynamic>>>>(
      future: widget.rehearsals,
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
                  padding: const EdgeInsets.symmetric(vertical: 8.0),
                  child: Text(
                    monthYear,
                    style: const TextStyle(
                        fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                ),
                ...daysMap.entries.map((dayEntry) {
                  String day = dayEntry.key;
                  List<dynamic> reherasalsList = dayEntry.value;

                  // Sort rehearsals by beginningDate
                  if (!widget.isCalendar) {
                    reherasalsList.sort((a, b) =>
                        DateTime.parse(a['beginningDate'])
                            .compareTo(DateTime.parse(b['beginningDate'])));
                  }

                  return Padding(
                    padding: const EdgeInsets.symmetric(vertical: 6.0),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Column(
                          children: [
                            Container(
                              width: 50,
                              alignment: Alignment.center,
                              margin: const EdgeInsets.only(top: 12),
                              child: Column(
                                children: [
                                  Text(
                                    getDayName(widget.isCalendar
                                        ? reherasalsList.first['date']
                                        : reherasalsList
                                            .first['beginningDate']),
                                    style: const TextStyle(
                                        fontSize: 15,
                                        fontWeight: FontWeight.bold),
                                  ),
                                  Text(
                                    day,
                                    style: const TextStyle(
                                        fontSize: 19,
                                        fontWeight: FontWeight.bold),
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
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: reherasalsList.map((rehearsal) {
                              return Container(
                                width: 500,
                                margin: const EdgeInsets.only(bottom: 8),
                                padding: const EdgeInsets.all(12),
                                decoration: BoxDecoration(
                                  color: widget.isCalendar
                                      ? colors[
                                          rehearsal['projectId'] % colors.length]
                                      : (rehearsal['accepted']
                                          ? Colors.green[100]
                                          : Colors.red[100]),
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                child: Row(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                  children: [
                                    Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        //TODO make the widget clikable to see the detail of the rehearsal
                                        Text(
                                          "${rehearsal['name']}",
                                          style: const TextStyle(fontSize: 18),
                                        ),
                                        Text(
                                          widget.isCalendar
                                              ? "${Utils.formatTimeString(rehearsal['time'])} - ${getEndTime(rehearsal['time'], rehearsal['duration'])}"
                                              : "${formatTime(rehearsal['beginningDate'])} - ${formatTime(DateTime.parse(rehearsal['beginningDate']).add(Utils.parseDuration(rehearsal['duration'])).toString())}",
                                          style: const TextStyle(
                                            fontSize: 14,
                                            fontStyle: FontStyle.italic,
                                          ),
                                        ),
                                      ],
                                    ),
                                    !widget.isCalendar
                                        ? GestureDetector(
                                            onTap: () {
                                              widget.accept!(rehearsal);
                                            },
                                            child: Icon(
                                              rehearsal['accepted']
                                                  ? Icons.close
                                                  : Icons.check,
                                              size: 30,
                                            ),
                                          )
                                        : const SizedBox(),
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
    );
  }
}
