import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/organizer/project/new_project.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/organizer/project/project_element.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:http/http.dart' as http;

/// List all the project in [projects], if the end date has past those project will be put in a archived section.
/// The individual projects are clickable, redirect to the information and gestion of the project. [refreshProjects] will be called when comming back to this page after clicking on a project.
/// If [isOrganizerPage] is set at true then a button is display to getto the page to create a new project.
class ProjectList extends StatelessWidget {
  final Future<List> projects;
  final VoidCallback refreshProjects;
  final bool isOrganizerPage;
  final FirebaseAuth? auth;
  final http.Client? client;

  const ProjectList({
    required this.projects,
    required this.refreshProjects,
    required this.isOrganizerPage,
    super.key,
    this.auth,
    this.client,
  });


  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List>(
      future: projects,
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
          final allProjects = snapshot.data!;
          final activeProjects = allProjects.where((project) {
            final now = DateTime.now();
            final today = DateTime(now.year, now.month, now.day);
            final endingDate = DateTime.tryParse(project['endingDate'] ?? '');
            return endingDate == null ||
                endingDate.isAfter(today) ||
                endingDate == today;
          }).toList();
          final archivedProjects = allProjects.where((project) {
            final now = DateTime.now();
            final endingDate = DateTime.tryParse(project['endingDate'] ?? '');
            return endingDate != null &&
                endingDate.isBefore(DateTime(now.year, now.month, now.day));
          }).toList();

          return ListView(
            children: [
              if (isOrganizerPage) ...[
                const SizedBox(height: 25),
                Center(
                  child: SizedBox(
                    width: 250,
                    child: ButtonCustom(
                      text: "Nouveau projet",
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) => const NewProjectPage()),
                        ).then((_) {
                          refreshProjects();
                        });
                      },
                    ),
                  ),
                ),
                const SizedBox(height: 25),
              ],
              if (activeProjects.isNotEmpty) ...[
                ...activeProjects.map((project) => ProjectElement(
                      id: project['id'],
                      organizerPage: isOrganizerPage,
                      onUpdate: refreshProjects,
                      client: client,
                      auth: auth,
                    )),
              ],
              if (activeProjects.isEmpty && !isOrganizerPage)
                const Padding(
                  padding: EdgeInsets.symmetric(vertical: 30, horizontal: 16),
                  child: Center(
                    child: Text(
                      'Vous n\'avez pas de projet actif pour le moment.',
                      style: TextStyle(
                        fontSize: 15.0,
                      ),
                    ),
                  ),
                ),
              if (archivedProjects.isNotEmpty) ...[
                ExpansionTile(
                  shape: Border.all(color: Colors.transparent),
                  title: const Text(
                    'Projets Archivés',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  initiallyExpanded: false,
                  children: archivedProjects
                      .map((project) => ProjectElement(
                            id: project['id'],
                            organizerPage: isOrganizerPage,
                            onUpdate: refreshProjects,
                            client: client,
                            auth: auth,
                          ))
                      .toList(),
                ),
              ],
            ],
          );
        } else {
          return const Center(
            child: Text('Aucun projet trouvé'),
          );
        }
      },
    );
  }
}
