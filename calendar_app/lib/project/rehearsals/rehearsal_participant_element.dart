import 'package:calendar_app/project/participants/participant_rehearsal_detail.dart';
import 'package:flutter/material.dart';

class ParticipantElement extends StatefulWidget {
  final int projectId; 
  final int rehearsalId;
  final int userId;
  final String firstName;
  final String lastName;
  final String email;
  //final List roles;
  final VoidCallback onUpdate;

  const ParticipantElement({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.userId,
    required this.firstName,
    required this.lastName,
    required this.email,
    //required this.roles,
    required this.onUpdate,
  });

  @override
  State<ParticipantElement> createState() => _ParticipantElementState();
}

class _ParticipantElementState extends State<ParticipantElement> {
  late List roles;

  @override
  void initState() {
    super.initState();
    //roles = widget.roles;
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 35.0),
      child: GestureDetector(
        onTap: () {
          Navigator.push(context,
                  MaterialPageRoute(builder: (context) => ParticpipantRehearsalDetailPage(projectId: widget.projectId,rehearsalId: widget.rehearsalId, userId: widget.userId, lastName: widget.lastName, firstName: widget.firstName, email: widget.email,)))
              .then((_) {
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
            padding: const EdgeInsets.all(10.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "${widget.lastName} ${widget.firstName}",
                  style: const TextStyle(
                    fontSize: 17,
                    color: Colors.black,
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
