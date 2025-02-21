import 'package:calendar_app/project/rehearsals/rehearsal_details.dart';
import 'package:flutter/material.dart';

class RehearsalElement extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final String name;
  final String? description;
  final String? date;
  final String? duration;
  final List participantsIds;
  final VoidCallback onUpdate;

  const RehearsalElement({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.name,
    required this.description,
    required this.date,
    required this.duration,
    required this.participantsIds,
    required this.onUpdate,
  });

  @override
  State<RehearsalElement> createState() => _RehearsalElementState();
}

class _RehearsalElementState extends State<RehearsalElement> {
  late String name;
  late String? description;
  late String? date;
  late String? duration;
  late List participantsIds;
  late Future<List>? users;

  @override
  void initState() {
    super.initState();
    //TODO nécessaire d'être ici ?
    name = widget.name;
    description = widget.description;
    date = widget.date;
    duration = widget.duration;
    participantsIds = widget.participantsIds; //TODO nécessaire
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5.0, horizontal: 16.0),
      child: GestureDetector(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => RehearsalDetailsPage(
                rehearsalId: widget.rehearsalId,
                projectId: widget.projectId,
                name: widget.name,
                description: widget.description,
                date: widget.date,
                duration: widget.duration,
                participantsIds: widget.participantsIds,
              ),
            ),
          ).then((_) {
            widget.onUpdate();
          });
        },
        child: Container(
          decoration: BoxDecoration(
            color: const Color(0xFFF2F2F2),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(),
          ),
          child: Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: 24.0, vertical: 12.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: Colors.black,
                  ),
                ),
                const SizedBox(height: 10),
                if (description != null && description != "")
                  Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: Text(
                      'Description : $description',
                      style: const TextStyle(
                        fontSize: 15,
                        color: Colors.black,
                      ),
                    ),
                  ),
                if (date != null)
                  Text(
                    'Date: $date',
                    style: const TextStyle(
                      fontSize: 13,
                      color: Colors.black54,
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
