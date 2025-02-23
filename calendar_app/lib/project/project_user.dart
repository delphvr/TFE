import 'package:calendar_app/organizer/project/project_element.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:convert';

class ProjectsUserPage extends StatefulWidget {
  const ProjectsUserPage({super.key});

  @override
  State<ProjectsUserPage> createState() => _ProjectsUserPageState();
}

class _ProjectsUserPageState extends State<ProjectsUserPage> {
  final user = FirebaseAuth.instance.currentUser!;
  late Future<List>? projects;

  @override
  void initState() {
    super.initState();
    projects = getProjects(context);
  }

  void logout() {
    FirebaseAuth.instance.signOut();
  }

  Future<List> getProjects(BuildContext context) async {
    final email = user.email;
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/user/$email';
    try {
      final response = await http.get(Uri.parse(url));

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
        Utils.errorMess('Erreur lors de la récupérations des projects',
            'Une erreur c\'est produite', context);
        return [];
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de la récupérations des projects',
          'Une erreur c\'est produite', context);
      return [];
    }
  }

  void refreshProjects() {
    setState(() {
      projects = getProjects(context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        actions: [
          IconButton(
              onPressed: logout,
              icon: const Icon(
                Icons.logout,
                size: 40,
              ))
        ],
      ),
      body: Center(
        child: Column(
          children: [
            Expanded(
              child: FutureBuilder<List>(
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
                    final projects = snapshot.data!;

                    return ListView.builder(
                      itemCount: projects.length,
                      itemBuilder: (context, index) {
                        return ProjectElement(
                          id: projects[index]['id'],
                          organizerPage: false,
                          onUpdate: refreshProjects,
                        );
                      },
                    );
                  } else {
                    return const Center(
                      child: Text('Aucun projet trouvé'),
                    );
                  }
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
