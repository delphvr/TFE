import 'package:calendar_app/components/project_list.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:convert';

/// Page with the list of projects for which the user connected is a part of
class ProjectsUserPage extends StatefulWidget {
  final http.Client? client;
  final FirebaseAuth? auth;
  const ProjectsUserPage({super.key, this.client, this.auth});

  @override
  State<ProjectsUserPage> createState() => _ProjectsUserPageState();
}

class _ProjectsUserPageState extends State<ProjectsUserPage> {
  late Future<List>? projects;
  http.Client get client => widget.client ?? http.Client();
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  late User user;
  final String errorTitle = 'Erreur lors de la récupérations des projects';

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    projects = getProjects(context);
  }

  /// Get the list of project the user is a part of from the backend.
  /// If an error occurs an error message will be display.
  Future<List> getProjects(BuildContext context) async {
    final email = user.email;
    final String url = '${dotenv.env['API_BASE_URL']}/projects/user/$email';
    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> userProjects = data.map((item) {
          return {
            'id': item['id'],
            'name': item['name'],
            'description': item['description'],
            'beginningDate': item['beginningDate'],
            'endingDate': item['endingDate'],
          };
        }).toList();
        return userProjects;
      } else {
        if (context.mounted) {
          Utils.errorMess(errorTitle,
              'Une erreur c\'est produite', context);
        }
        return [];
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess(errorTitle,
            'Une erreur c\'est produite', context);
      }
      return [];
    }
  }

  /// Update the variable [projects] with list of project the user is a part of.
  Future<void> refreshProjects() async {
    setState(() {
      projects = getProjects(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Center(
        child: Column(
          children: [
            Expanded(
              child: RefreshIndicator(
                onRefresh: refreshProjects, 
                child: ProjectList(
                  projects: projects!,
                  refreshProjects: refreshProjects,
                  isOrganizerPage: false,
                  auth: widget.auth,
                  client: widget.client,
                ),
              ),
            ),
          ],
        ),
      ),
      selectedIndex: 0,
    );
  }
}
