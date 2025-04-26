import 'package:calendar_app/auth/register.dart';
import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:multi_select_flutter/multi_select_flutter.dart';

/// Page to update the profile informations (name, email and professions).
class ProfileModificationPage extends StatefulWidget {
  final int id;
  final String firstname;
  final String lastname;
  final String email;
  final List professions;
  final FirebaseAuth? auth;
  final http.Client? client;

  const ProfileModificationPage({
    super.key,
    required this.id,
    required this.firstname,
    required this.lastname,
    required this.email,
    required this.professions,
    this.auth,
    this.client,
  });

  @override
  State<ProfileModificationPage> createState() =>
      _ProfileModificationPageState();
}

class _ProfileModificationPageState extends State<ProfileModificationPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  final String errorTitle = 'Erreur lors de la modification du profil';

  late TextEditingController firstNameController;
  late TextEditingController lastNameController;
  late TextEditingController emailController;
  List<Profession> professions = [];
  List<Profession> selectedProfessions = [];
  List<MultiSelectItem<Profession>> items = [];

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    firstNameController = TextEditingController(text: widget.firstname);
    lastNameController = TextEditingController(text: widget.lastname);
    emailController = TextEditingController(text: widget.email);
    getProfessions(context);
    getUserProfessions(context);
  }

  @override
  void dispose() {
    firstNameController.dispose();
    lastNameController.dispose();
    emailController.dispose();
    super.dispose();
  }

  DateTime? selectedDate;

  /// Get the list of possible professions from the backend and update the variables [professions] and [items] with it.
  /// If an error occurs does nothing.
  Future<void> getProfessions(BuildContext context) async {
    String url = '${dotenv.env['API_BASE_URL']}/professions';
    try {
      final response = await client.get(Uri.parse(url));
      if (response.statusCode == 200) {
        List<dynamic> jsonData = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          professions = jsonData.isEmpty
              ? []
              : jsonData.map((json) => Profession.fromJson(json)).toList();
          items = professions
              .map((profession) =>
                  MultiSelectItem<Profession>(profession, profession.name))
              .toList();
        });
      }
    } catch (e) {
      return;
    }
  }

  /// Update the variable [selectedProfessions] with the list of profession linked to the user (given by the backend).
  /// If an error occurs display an error message.
  void getUserProfessions(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/users/${user.email!}/professions';
    try {
      final response = await client.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          selectedProfessions =
              data.map((name) => Profession(name: name)).toList();
        });
      } else {
        if (context.mounted) {
          Utils.errorMess('Une erreur est survenue',
              'Merci de réessayer plus tard.', context);
        }
        setState(() {
          selectedProfessions = [];
        });
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Une erreur est survenue',
            'Merci de réessayer plus tard.', context);
      }
      setState(() {
        selectedProfessions = [];
      });
    }
  }

  /// Send the updated information (name, email, profssions) about the use to the backend to be saved.
  /// Check that the names and email field are not empty and that the email is in a valid format.
  /// If an error occurs an error message will be displayed.
  void update(BuildContext context) async {
    final firstname = firstNameController.text;
    final lastname = lastNameController.text;
    final email = emailController.text;

    if (firstname.isEmpty || lastname.isEmpty || email.isEmpty) {
      Utils.errorMess(errorTitle, 'Merci de remplir tout les champs', context);
      return;
    } else if (!Utils.isValidEmail(emailController.text)) {
      Utils.errorMess(errorTitle, 'Email non valide', context);
      return;
    }

    final String url = '${dotenv.env['API_BASE_URL']}/users/${widget.id}';

    final Map<String, dynamic> requestBody = {
      "firstName": firstname,
      "lastName": lastname,
      "email": email,
      "professions": selectedProfessions.map((p) => p.name).toList(),
    };
    try {
      final response = await client.put(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 200) {
        if (context.mounted) {
          Navigator.pop(context);
        }
      } else {
        if (context.mounted) {
        Utils.errorMess(
            errorTitle,
            'Erreur lors de la modification. Merci de réessayez plus tard.',
            context);
        }
      }
    } catch (e) {
      if (context.mounted) {
      Utils.errorMess(
          errorTitle, 'Impossible de se connecter au serveur.', context);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
        body: Center(
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  'Modification du Compte',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 30,
                  ),
                ),

                const SizedBox(height: 20),

                TextFieldcustom(
                  labelText: 'Prénom',
                  controller: firstNameController,
                  obscureText: false,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 15),

                TextFieldcustom(
                  labelText: 'Nom de famille',
                  controller: lastNameController,
                  obscureText: false,
                  keyboardType: TextInputType.text,
                ),

                const SizedBox(height: 15),

                //email
                TextFieldcustom(
                  key: const Key('emailField'),
                  labelText: 'Email',
                  controller: emailController,
                  obscureText: false,
                  keyboardType: TextInputType.emailAddress,
                ),

                const SizedBox(height: 15),

                BottomSheetSelector<Profession>(
                  items: professions,
                  selectedItems: selectedProfessions,
                  onSelectionChanged: (selectedList) {
                    setState(() {
                      selectedProfessions = selectedList;
                    });
                  },
                  title: "Sélectionnez vos professions",
                  buttonLabel: "Valider",
                  itemLabel: (profession) => profession.name,
                  textfield: "Professions",
                ),

                const SizedBox(height: 15),
                ButtonCustom(
                  text: 'Modifier',
                  onTap: () => update(context),
                ),
              ],
            ),
          ),
        ),
        selectedIndex: 3);
  }
}
