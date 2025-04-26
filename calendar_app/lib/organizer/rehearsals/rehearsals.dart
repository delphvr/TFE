import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/organizer/rehearsals/add_rehearsal.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_element.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/// Page with the list of rehearsal on the project.
/// Plus a button to get to the page to add a project.
class RehearsalPage extends StatefulWidget {
  final int projectId;
  final String projectName;
  final http.Client? client;
  final FirebaseAuth? auth;

  const RehearsalPage({
    super.key,
    required this.projectId,
    required this.projectName,
    this.client, 
    this.auth,
  });

  @override
  State<RehearsalPage> createState() => _RehearsalPage();
}

class _RehearsalPage extends State<RehearsalPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  late Future<List>? rehearsals;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    rehearsals = getRehearsals(context);
  }

  /// Get the list of rehearsal of the project with id [widget.projectId] from the backend.
  /// If an error occurs return an empty list.
  Future<List> getRehearsals(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/rehearsals';
    try {
      final response = await client.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> userProjects = data.map((item) {
          return {
            'id': item['id'],
            'name': item['name'],
            'description': item['description'],
            'date': item['date'],
            'time': item['time'],
            'duration': item['duration'],
            'projectId': item['projectId'],
            'location': item['location'],
            'participantsIds': item['participantsIds']
          };
        }).toList();
        return userProjects;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  /// Update the variable [rehearsals] with the list of rehearsal on the project with id [widget.projectId].
  void refreshRehearsals() {
    setState(() {
      rehearsals = getRehearsals(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: SingleChildScrollView(
        child: Column(
          children: [
            Text(
              "Répétitions du projet ${widget.projectName}",
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 27,
              ),
            ),
            const SizedBox(height: 25),
            ButtonCustom(
              text: 'Ajouter une répétition',
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => AddRehearsal(
                      projectId: widget.projectId,
                      projectName: widget.projectName,
                    ),
                  ),
                ).then((_) {
                  setState(() {
                    rehearsals = getRehearsals(context);
                  });
                });
              },
            ),
            const SizedBox(height: 25),
            FutureBuilder<List>(
              future: rehearsals,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(
                    child: CircularProgressIndicator(),
                  );
                } else if (snapshot.hasError) {
                  return Center(
                    child: Text("Erreur: ${snapshot.error}"),
                  );
                } else if (snapshot.hasData) {
                  final rehearsals = snapshot.data!;
                  return ListView.builder(
                    shrinkWrap: true, 
                    physics: const ClampingScrollPhysics(),
                    itemCount: rehearsals.length,
                    itemBuilder: (context, index) {
                      return RehearsalElement(
                        rehearsalId: rehearsals[index]['id'],
                        name: rehearsals[index]['name'],
                        description: rehearsals[index]['description'],
                        date: Utils.formatDateString(rehearsals[index]['date']),
                        time: rehearsals[index]['time'],
                        duration: rehearsals[index]['duration'],
                        location: rehearsals[index]['location'],
                        projectId: rehearsals[index]['projectId'],
                        participantsIds: rehearsals[index]['participantsIds'],
                        organizerPage: true,
                        onUpdate: refreshRehearsals,
                      );
                    },
                  );
                } else {
                  return const Center(
                    child: Text('Aucune répétition trouvée'),
                  );
                }
              },
            ),
          ],
        ),
      ),
      selectedIndex: 1,
    );
  }
}
