import 'package:calendar_app/organizer/rehearsals/rehearsal_details.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_presences.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

class CalendarList extends StatefulWidget {
  final int? projectId;
  final Function? accept;
  final Future<Map<String, Map<String, List<dynamic>>>>? rehearsals;
  final bool isCalendar;
  final Future<Map<int, Map<int, bool>>>? participations;
  final Future<Map<int, bool>>? userPresences;
  final Function? updatePresences;

  const CalendarList({
    super.key,
    this.projectId,
    required this.rehearsals,
    this.accept,
    required this.isCalendar,
    this.participations,
    this.userPresences,
    this.updatePresences,
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

  Future<String> getParticipationProportions(int rehearsalId) async {
    if (widget.participations == null) return "";

    final participationData = await widget.participations!;
    if (!participationData.containsKey(rehearsalId)) return "";

    Map<int, bool> partic = participationData[rehearsalId]!;
    int total = partic.length;
    int accepted = partic.values.where((accepted) => accepted).length;

    if (total == 0) return "0/0";

    return "$accepted/$total";
  }

  Future<bool> isUserPresent(int rehearsalId) async {
    if (widget.userPresences == null) return false;
    final presenceMap = await widget.userPresences!;
    return presenceMap[rehearsalId] ?? false;
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
                              return GestureDetector(
                                onTap: () {
                                  if (!widget.isCalendar) {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (context) => PresencesPage(
                                          rehearsalId: rehearsal['rehearsalId'],
                                          name: rehearsal['name'],
                                          isCalendar: true,
                                        ),
                                      ),
                                    );
                                  } else {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (context) =>
                                            RehearsalDetailsPage(
                                                rehearsalId:
                                                    rehearsal['rehearsalId'],
                                                projectId: rehearsal['projectId'],
                                                name: rehearsal['name'],
                                                description:
                                                    rehearsal['description'],
                                                date: rehearsal['date'],
                                                time: rehearsal['time'],
                                                duration: rehearsal['duration'],
                                                participantsIds: const [],
                                                organizerPage: false),
                                      ),
                                    );
                                  }
                                },
                                child: Container(
                                  width: 500,
                                  margin: const EdgeInsets.only(bottom: 8),
                                  padding: const EdgeInsets.all(12),
                                  decoration: BoxDecoration(
                                    color: widget.isCalendar
                                        ? colors[rehearsal['projectId'] %
                                            colors.length]
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
                                          Text(
                                            "${rehearsal['name']}",
                                            style:
                                                const TextStyle(fontSize: 18),
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
                                          ? FutureBuilder<String>(
                                              future:
                                                  getParticipationProportions(
                                                      rehearsal['rehearsalId']),
                                              builder: (context, snapshot) {
                                                return Text(snapshot.data ?? "",
                                                    style: const TextStyle(
                                                        fontSize: 18));
                                              },
                                            )
                                          : const SizedBox(),
                                      !widget.isCalendar
                                          ? GestureDetector(
                                              onTap: () {
                                                widget.accept!(rehearsal);
                                              },
                                              child: Icon(
                                                rehearsal['accepted']
                                                    ? Icons.check
                                                    : Icons.close,
                                                size: 30,
                                              ),
                                            )
                                          : const SizedBox(),
                                      widget.isCalendar
                                          ? FutureBuilder<bool>(
                                              future: isUserPresent(
                                                  rehearsal['rehearsalId']),
                                              builder: (context, snapshot) {
                                                if (!snapshot.hasData) {
                                                  return const SizedBox(
                                                      width: 30, height: 30);
                                                }
                                                final present = snapshot.data!;
                                                return GestureDetector(
                                                  onTap: () {
                                                    widget.updatePresences!(
                                                        context,
                                                        rehearsal[
                                                            'rehearsalId'],
                                                        !present);
                                                  },
                                                  child: Icon(
                                                    present
                                                        ? Icons.check
                                                        : Icons.close,
                                                    size: 30,
                                                  ),
                                                );
                                              },
                                            )
                                          : const SizedBox(),
                                    ],
                                  ),
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
