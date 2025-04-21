import 'package:calendar_app/components/project_list.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:convert';

/// Page with the list of projects for which the user connected is an organizer
/// has a button to get to the page to create new project
class ProjectOrganizerPage extends StatefulWidget {
  final FirebaseAuth? auth;
  final http.Client? client;
  const ProjectOrganizerPage({
    super.key,
    this.auth,
    this.client,
  });

  @override
  State<ProjectOrganizerPage> createState() => _ProjectOrganizerPageState();
}

class _ProjectOrganizerPageState extends State<ProjectOrganizerPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  final String errorTitle = 'Erreur lors de la récupérations des projects';
  late Future<List>? projects;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    projects = getProjects(context);
  }

  /// Get from the backend the list of projects for which the user is an organizer.
  /// If an error occurs display an error message.
  Future<List> getProjects(BuildContext context) async {
    final email = user.email;
    final String url =
        '${dotenv.env['API_BASE_URL']}/userProjects/organizer/$email';
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
        //if (mounted) {
        //  Navigator.pop(context);
        //}
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

  /// update the variable [projects] by getting the project from which the user is an organizer from the database. 
  /// If an error occurs display an error message.
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
                  isOrganizerPage: true,
                  client: widget.client,
                  auth: widget.auth,
                ),
              )),
            ],
          ),
        ),
        selectedIndex: 1);
  }
}
