import 'package:flutter/material.dart';
import 'package:calendar_app/organizer/project/project_element.dart';

class ProjectList extends StatelessWidget {
  final Future<List> projects;
  final VoidCallback refreshProjects;
  final bool isOrganizerPage;

  const ProjectList({
    required this.projects,
    required this.refreshProjects,
    required this.isOrganizerPage,
    super.key,
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
              if (activeProjects.isNotEmpty) ...[
                ...activeProjects.map((project) => ProjectElement(
                      id: project['id'],
                      organizerPage: isOrganizerPage,
                      onUpdate: refreshProjects,
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
