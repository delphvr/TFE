import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/organizer/rehearsals/rehearsal_element.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class UserRehearsalPage extends StatefulWidget {
  final int projectId;
  final String projectName;

  const UserRehearsalPage({
    super.key,
    required this.projectId,
    required this.projectName,
  });

  @override
  State<UserRehearsalPage> createState() => _UserRehearsalPageState();
}

class _UserRehearsalPageState extends State<UserRehearsalPage> {
  final user = FirebaseAuth.instance.currentUser!;
  late Future<List>? rehearsals;

  @override
  void initState() {
    super.initState();
    rehearsals = getRehearsals(context);
  }

  Future<List> getRehearsals(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/${user.email}/projects/${widget.projectId}/rehearsals';
    try {
      final response = await http.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        final List<dynamic> userRehearsals = data.map((item) {
          return {
            'id': item['id'],
            'name': item['name'],
            'description': item['description'],
            'date': item['date'],
            'duration': item['duration'],
            'projectId': item['projectId'],
            'participantsIds': item['participantsIds']
          };
        }).toList();
        return userRehearsals;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  void refreshRehearsals() {
    setState(() {
      rehearsals = getRehearsals(context);
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
                "Vos répétitions pour le projet ${widget.projectName}",
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 27,
                ),
              ),
              const SizedBox(height: 25),
              Flexible(
                child: FutureBuilder<List>(
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
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: rehearsals.length,
                        itemBuilder: (context, index) {
                          return RehearsalElement(
                            rehearsalId: rehearsals[index]['id'],
                            name: rehearsals[index]['name'],
                            description: rehearsals[index]['description'],
                            date: rehearsals[index]['date'],
                            duration: rehearsals[index]['duration'],
                            projectId: rehearsals[index]['projectId'],
                            participantsIds: rehearsals[index]
                                ['participantsIds'],
                            organizerPage: false,
                            onUpdate: refreshRehearsals,
                          );
                        },
                      );
                    } else {
                      return const Center(
                        child: Text('Aucune répétition trouvé'),
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
