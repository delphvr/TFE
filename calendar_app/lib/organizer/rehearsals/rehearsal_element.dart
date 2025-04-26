import 'package:calendar_app/organizer/rehearsals/rehearsal_details.dart';
import 'package:calendar_app/utils.dart';
import 'package:flutter/material.dart';

/// Display the information (name, description, date, duration) about the rehearsal with id [rehearsalId].
/// Clickable widget to get to the details page about the rehearsal.
class RehearsalElement extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final String name;
  final String? description;
  final String? date;
  final String? time;
  final String? duration;
  final String? location;
  final List participantsIds;
  final bool organizerPage;
  final VoidCallback onUpdate;

  const RehearsalElement({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.name,
    required this.description,
    required this.date,
    required this.time,
    required this.duration,
    required this.location,
    required this.participantsIds,
    required this.organizerPage,
    required this.onUpdate,
  });

  @override
  State<RehearsalElement> createState() => _RehearsalElementState();
}

class _RehearsalElementState extends State<RehearsalElement> {

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
                time: widget.time,
                duration: widget.duration,
                location: widget.location,
                participantsIds: widget.participantsIds,
                organizerPage: widget.organizerPage
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
                  widget.name,
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: Colors.black,
                  ),
                ),
                const SizedBox(height: 10),
                if (widget.description != null && widget.description != "")
                  Padding(
                    padding: const EdgeInsets.only(bottom: 2.5),
                    child: Text(
                      'Description : ${widget.description}',
                      style: const TextStyle(
                        fontSize: 15,
                        color: Colors.black,
                      ),
                    ),
                  ),
                if (widget.date != null)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 2.5),
                    child: Text(
                      'Date : ${widget.date}',
                      style: const TextStyle(
                        fontSize: 15,
                        color: Colors.black,
                      ),
                    ),
                  ),
                if (widget.duration != null)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 2.5),
                    child: Text(
                      'Durée : ${Utils.formatDuration(widget.duration!)}',
                      style: const TextStyle(
                        fontSize: 15,
                        color: Colors.black,
                      ),
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
