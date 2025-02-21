import 'package:calendar_app/project/participants/particpipant_modification.dart';
import 'package:flutter/material.dart';

class UsersElement extends StatefulWidget {
  final int projectId;
  final int userId;
  final String firstName;
  final String lastName;
  final String email;
  //final List roles;
  final VoidCallback onUpdate;

  const UsersElement({
    super.key,
    required this.projectId,
    required this.userId,
    required this.firstName,
    required this.lastName,
    required this.email,
    //required this.roles,
    required this.onUpdate,
  });

  @override
  State<UsersElement> createState() => _UsersElementState();
}

class _UsersElementState extends State<UsersElement> {
  late List roles;

  @override
  void initState() {
    super.initState();
    //roles = widget.roles;
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 25.0),
      child: GestureDetector(
        onTap: () {
          Navigator.push(context,
                  MaterialPageRoute(builder: (context) => ParticpipantModificationPage(projectId: widget.projectId, userId: widget.userId, lastName: widget.lastName, firstName: widget.firstName, email: widget.email,)))
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
