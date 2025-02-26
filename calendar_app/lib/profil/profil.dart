import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/organizer/roles/role_and_participant_element.dart';
import 'package:calendar_app/profil/profil_modification.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

class ProfilPage extends StatefulWidget {
  const ProfilPage({
    super.key,
  });

  @override
  State<ProfilPage> createState() => _ProfilPageSate();
}

class _ProfilPageSate extends State<ProfilPage> {
  final user = FirebaseAuth.instance.currentUser!;
  String? firstName;
  String? lastName;
  String? email;
  int? id;
  Future<List>? professions;
  bool? isOrganizer;

  @override
  void initState() {
    super.initState();
    initUserData(context);
    initUserProfessions(context);
    initIsOrganizer(context);
  }

  void initUserData(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users?email=${user.email!}';
    try {
      final response = await http.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final Map<String, dynamic> data =
            json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          firstName = data['firstName'];
          lastName = data['lastName'];
          email = data['email'];
          id = data['id'];
        });
      } else {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
    } catch (e) {
      Utils.errorMess(
          'Une erreur est survenue', 'Merci de réessayer plus tard.', context);
    }
  }

  void initIsOrganizer(BuildContext context) async {
    String url = '${dotenv.env['API_BASE_URL']}/users/organizer/${user.email!}';
    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
      );
      if (response.statusCode == 200) {
        setState(() {
          isOrganizer = response.body == "true";
        });
        SharedPreferences prefs = await SharedPreferences.getInstance();
        prefs.setBool('isOrganizer', isOrganizer!);
      } else {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
    } catch (e) {
      Utils.errorMess(
          'Une erreur est survenue', 'Merci de réessayer plus tard.', context);
    }
  }

  void initUserProfessions(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/${user.email!}/professions';
    try {
      final response = await http.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          professions = Future.value(data.toList());
        });
      } else {
        professions = Future.value([]);
      }
    } catch (e) {
      Utils.errorMess(
          'Une erreur est survenue', 'Merci de réessayer plus tard.', context);
    }
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
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 25),
            Align(
              alignment: Alignment.centerLeft,
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 35),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Nom : $lastName',
                      style: const TextStyle(
                        fontSize: 20,
                      ),
                    ),
                    const SizedBox(height: 10),
                    Text(
                      'Prénom : $firstName',
                      style: const TextStyle(
                        fontSize: 20,
                      ),
                    ),
                    const SizedBox(height: 10),
                    Text(
                      'email : $email',
                      style: const TextStyle(
                        fontSize: 20,
                      ),
                    ),
                    const SizedBox(height: 10),
                    const Text(
                      "Professions :",
                      style: TextStyle(
                        fontSize: 20,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 10),
            Flexible(
              child: FutureBuilder<List>(
                future: professions,
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
                    final professions = snapshot.data!;

                    return ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: professions.length,
                      itemBuilder: (context, index) {
                        return RoleOrParticipantElement(
                          name: professions[index],
                        );
                      },
                    );
                  } else {
                    return const Center(
                      child: Text('Aucune professions trouvée'),
                    );
                  }
                },
              ),
            ),
            const SizedBox(height: 10),
            ButtonCustom(
              text: 'Modifier',
              onTap: () async {
                if (professions != null) {
                  List professionsList = await professions!; 
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ProfileModificationPage(
                        email: email!,
                        firstname: firstName!,
                        lastname: lastName!,
                        id: id!,
                        professions:professionsList, 
                        isOrganizer: isOrganizer!,
                      ),
                    ),
                  ).then((_) {
                    initUserData(context);
                    initUserProfessions(context);
                  });
                } else {
                  Utils.errorMess('Erreur',
                      'Impossible de charger les professions.', context);
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}
