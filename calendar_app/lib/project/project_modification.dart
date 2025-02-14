import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/project/add_participant.dart';
import 'package:calendar_app/project/update_project.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';

class ProjectModificationPage extends StatefulWidget {
  final int id;
  final String name;
  final String? description;
  final String? beginningDate;
  final String? endingDate;

  const ProjectModificationPage({
    super.key,
    required this.id,
    required this.name,
    this.description,
    this.beginningDate,
    this.endingDate,
  });

  @override
  State<ProjectModificationPage> createState() => _ProjectModificationPage();
}

class _ProjectModificationPage extends State<ProjectModificationPage> {
  final user = FirebaseAuth.instance.currentUser!;

  late String name;
  late String? description;
  late String? beginningDate;
  late String? endingDate;

  @override
  void initState() {
    super.initState();
    name = widget.name;
    description = widget.description;
    beginningDate = widget.beginningDate;
    endingDate = widget.endingDate;
  }

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        actions: [
          IconButton(
            onPressed: () {
              logout(() {
                Navigator.of(context).pushAndRemoveUntil(
                  MaterialPageRoute(builder: (context) => const Auth()),
                  (route) => false,
                );
              });
            },
            icon: const Icon(
              Icons.logout,
              size: 40,
            ),
          ),
        ],
      ),
      body: Align(
          alignment: Alignment.topCenter,
          child: SingleChildScrollView(
              child: Column(
            children: [
              Text(
                name,
                style: const TextStyle(
                  fontSize: 30,
                ),
              ),
              const SizedBox(height: 25),
              Align(
                alignment: Alignment.centerLeft,
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 35),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Description: ${description != null && description != '' ? description : "-"}",
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        'Date de début: ${beginningDate != null ? Utils.formatDate(beginningDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(
                        'Date de fin: ${endingDate != null ? Utils.formatDate(endingDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 20,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 25),
              ButtonCustom(
                text: 'Modifier',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => UpdateProjectPage(
                        id: widget.id,
                        name: name,
                        description: description,
                        beginningDate: beginningDate,
                        endingDate: endingDate,
                      ),
                    ),
                  ).then((updatedProject) {
                    if (updatedProject != null) {
                      setState(() {
                        name = updatedProject['name'];
                        description = updatedProject['description'];
                        beginningDate = updatedProject['beginningDate'];
                        endingDate = updatedProject['endingDate'];
                      });
                    }
                  });
                },
              ),
              const SizedBox(height: 25),
              const Align(
                alignment: Alignment.centerLeft,
                child: Padding(
                  padding: EdgeInsets.symmetric(horizontal: 35),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Participants",
                        style: TextStyle(
                          fontSize: 20,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 25),
              ButtonCustom(
                text: 'Ajouter des personnes',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => AddParticipant(
                        projectName: name,
                      ),
                    ),
                  ).then((_) {
                    setState(() {});
                  });
                },
              ),
            ],
          ))),
    );
  }
}
