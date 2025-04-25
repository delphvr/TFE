import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/main.dart';
import 'package:calendar_app/organizer/roles/role_and_participant_element.dart';
import 'package:calendar_app/disponibilities/disponibilities.dart';
import 'package:calendar_app/profil/profil_modification.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/// Profile Page
/// Can see the user last name, first name, email and the list of his proffesions
/// Has acces to a button to modify those information, 
/// a button to deal with his availabilities 
/// and a button to delete his account.
class ProfilPage extends StatefulWidget {
  final FirebaseAuth? auth;
  final http.Client? client;

  const ProfilPage({
    super.key,
    this.auth,
    this.client,
  });

  @override
  State<ProfilPage> createState() => _ProfilPageSate();
}

class _ProfilPageSate extends State<ProfilPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;

  //final user = FirebaseAuth.instance.currentUser!;
  String? firstName;
  String? lastName;
  String? email;
  int? id;
  Future<List>? professions;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    initUserData(context);
    initUserProfessions(context);
  }

  /// Get the information of the user from the backend.
  /// Udates the variables [firstName], [lastName], [email] and [id] with the retreived data.
  /// 
  /// If an error occurcs display an error message.
  void initUserData(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users?email=${user.email!}';
    try {
      final response = await client.get(Uri.parse(url));
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
        if (context.mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard.', context);
        }
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
    }
  }

  /// Get the list professions of the connected user from the backend and update the variable [professions] with this list.
  /// If an error occurcs display an error message.
  void initUserProfessions(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/${user.email!}/professions';
    try {
      final response = await client.get(Uri.parse(url)); 
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          professions = Future.value(data.toList());
        });
      } else {
        professions = Future.value([]);
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
    }
  }

  /// Delete the acount of the connected user.
  /// Delete the user from firebase and from the database.
  /// If an error occurcs display an error message.
  void deleteAcount() async {
    try {
      await auth.currentUser!.delete();
      final String url = '${dotenv.env['API_BASE_URL']}/users/${user.email!}';
      await client.delete(Uri.parse(url));
      if (mounted) {
        Navigator.of(context).pushAndRemoveUntil(
          MaterialPageRoute(builder: (context) => MyApp(auth: auth, client: client,)),
          (route) => false,
        );
      }
    } on FirebaseAuthException catch (e) {
      if (e.code == "requires-recent-login") {
        if (mounted) {
          Utils.errorMess(
              'Erreur lors de la suppression du compte',
              'Cette opération est sensible et nécessite une authentification récente. Reconnectez-vous avant de réessayer cette demande.',
              context);
        }
      } else {
        if (mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard.', context);
        }
      }
    } catch (e) {
      if (mounted) {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      auth: auth,
      client: client, 
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
                    FutureBuilder<List<dynamic>>(
                      future: professions,
                      builder: (context, snapshot) {
                        if (!snapshot.hasData || snapshot.data!.isEmpty) {
                          return const Text(
                            "Professions : -",
                            style: TextStyle(fontSize: 20),
                          );
                        }
                        return const Text(
                          "Professions :",
                          style: TextStyle(fontSize: 20),
                        );
                      },
                    )
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
                  if (context.mounted) {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => ProfileModificationPage(
                          email: email!,
                          firstname: firstName!,
                          lastname: lastName!,
                          id: id!,
                          professions: professionsList,
                        ),
                      ),
                    ).then((_) {
                      if (context.mounted) {
                        initUserData(context);
                        initUserProfessions(context);
                      }
                    });
                  }
                }
              },
            ),
            const SizedBox(height: 15),
            ButtonCustom(
              text: 'Gérer mes disponibilité',
              onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) => const DisponibilitiesPage()),
                        );
                      },
            ),
            const SizedBox(height: 15),
            ButtonCustom(
              text: 'Supprimer mon compte',
              onTap: () {
                Utils.confirmation(
                    'Action Irrévesible',
                    'Êtes-vous sûre de vouloir supprimer votre compte ?',
                    deleteAcount,
                    context);
              },
            ),
          ],
        ),
      ),
      selectedIndex: 3,
    );
  }
}
