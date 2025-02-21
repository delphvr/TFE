import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/project/participants/add_participant.dart';
import 'package:calendar_app/project/participants/participant_element.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ParticipantsPage extends StatefulWidget {
  final int id;
  final String name;

  const ParticipantsPage({
    super.key,
    required this.id,
    required this.name,
  });

  @override
  State<ParticipantsPage> createState() => _ParticipantsPage();
}

class _ParticipantsPage extends State<ParticipantsPage> {
  final user = FirebaseAuth.instance.currentUser!;
  late Future<List>? users;

  @override
  void initState() {
    super.initState();
    users = getUsersOnProject(context);
  }

  Future<List> getUsersOnProject(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/userProjects/${widget.id}';
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> userProjects = data.map((item) {
          return {
            'id': item['id'],
            'firstName': item['firstName'],
            'lastName': item['lastName'],
            'email': item['email'],
          };
        }).toList();
        return userProjects;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  void refreshUsers() {
    setState(() {
      users = getUsersOnProject(context);
    });
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
          child: Column(
            children: [
              Text(
                "Participants sur le projet ${widget.name}",
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 27,
                ),
              ),
              const SizedBox(height: 25),
              ButtonCustom(
                text: 'Ajouter un participant',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => AddParticipant(
                        projectId: widget.id,
                        projectName: widget.name,
                      ),
                    ),
                  ).then((_) {
                    setState(() {
                      users = getUsersOnProject(context);
                    });
                  });
                },
              ),
              const SizedBox(height: 25),
              Flexible(
                child: FutureBuilder<List>(
                  future: users,
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
                      final users = snapshot.data!;

                      return ListView.builder(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: users.length,
                        itemBuilder: (context, index) {
                          return UsersElement(
                            projectId: widget.id,
                            userId: users[index]['id'],
                            firstName: users[index]['firstName'],
                            lastName: users[index]['lastName'],
                            email: users[index]['email'],
                            //roles: users[index]['roles'],
                            onUpdate: refreshUsers,
                          );
                        },
                      );
                    } else {
                      return const Center(
                        child: Text('Aucun participant trouvé'),
                      );
                    }
                  },
                ),
              ),
            ],
          )),
    );
  }
}
