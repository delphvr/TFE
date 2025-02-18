import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/project/role_element.dart';
import 'package:calendar_app/project/role_modification.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ParticpipantModificationPage extends StatefulWidget {
  final int projectId;
  final int userId;
  final String firstName;
  final String lastName;
  final String email;

  const ParticpipantModificationPage({
    super.key,
    required this.projectId,
    required this.userId,
    required this.firstName,
    required this.lastName,
    required this.email,
  });

  @override
  State<ParticpipantModificationPage> createState() =>
      _ParticpipantModificationPage();
}

class _ParticpipantModificationPage
    extends State<ParticpipantModificationPage> {
  final user = FirebaseAuth.instance.currentUser!;

  late Future<List>? roles;

  @override
  void initState() {
    super.initState();
    roles = getRoles(context);
  }

  Future<List> getRoles(BuildContext context) async {
    final String url = '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}/roles'; 
    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        return data;
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  void refreshRoles() {
    setState(() {
      roles = getRoles(context);
    });
  }

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  void deleteParticipant() async{
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}';
    try {
      final response = await http.delete(Uri.parse(url));

      if (response.statusCode != 204) {
        Utils.errorMess('Erreur lors de la suppression du participant', 'Merci de réessayer plus tard', context);
      }else{
        Navigator.pop(context);
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de la suppression du participant', 'Merci de réessayer plus tard', context);
    }
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
            Text(
              "${widget.lastName} ${widget.firstName}",
              textAlign: TextAlign.center,
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
                      widget.email,
                      style: const TextStyle(
                        fontSize: 20,
                      ),
                    ),
                    const SizedBox(height: 10),
                    const Text(
                      "Rôles",
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
                future: roles,
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
                    final roles = snapshot.data!;
        
                    return ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: roles.length,
                      itemBuilder: (context, index) {
                        return RoleElement(
                          projectId: widget.projectId,
                          userId: widget.userId,
                          role: roles[index],
                          onUpdate: refreshRoles,
                        );
                      },
                    );
                  } else {
                    return const Center(
                      child: Text('Aucun rôle trouvé'),
                    );
                  }
                },
              ),
            ),
            const SizedBox(height: 25),
            ButtonCustom(
              text: 'Ajouter un rôle',
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => RoleModificationPage(projectId: widget.projectId, userId: widget.userId, firstName: widget.firstName, lastName: widget.lastName,)),
                ).then((_) {
                  refreshRoles();
                });
              },
            ),
            const SizedBox(height: 25),
              ButtonCustom(
                text: 'Suprimmer le partcipant',
                onTap: () {
                  Utils.confirmation('Action Irrévesible', 'Êtes-vous sûre de vouloir supprimer le participant du projet ?', deleteParticipant, context);
                },
              ),
          ],
        ),
      ),
    );
  }
}
