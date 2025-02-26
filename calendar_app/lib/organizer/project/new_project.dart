import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class NewProjectPage extends StatefulWidget {
  const NewProjectPage({super.key});

  @override
  State<NewProjectPage> createState() => _NewProjectPageState();
}

class _NewProjectPageState extends State<NewProjectPage> {
  final user = FirebaseAuth.instance.currentUser!;

  final projectNameController = TextEditingController();
  final descriptionController = TextEditingController();
  final beginningDateController = TextEditingController();
  final endingDateController = TextEditingController();

  DateTime? _selectedBeginningDate;
  DateTime? _selectedEndingDate;

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  void save(BuildContext context) async {
    final projectName = projectNameController.text;
    final description = descriptionController.text;
    final beginningDate = Utils.formatDateTime(_selectedBeginningDate);
    final endingDate = Utils.formatDateTime(_selectedEndingDate);

    if (projectName.isEmpty) {
      Utils.errorMess('Erreur lors de la création du project',
          'Veuillez donner un nom au project.', context);
      return;
    }

    if (beginningDate.isNotEmpty && endingDate.isNotEmpty) {
      if (DateTime.parse(beginningDate).isAfter(DateTime.parse(endingDate))) {
        Utils.errorMess(
            'Erreur lors de la création du project',
            'La date de fin du projet ne peut pas avoir lieu avant la date de début.',
            context);
        return;
      }
      if (DateTime.now().isAfter(DateTime.parse(endingDate))){
        Utils.errorMess(
            'Erreur lors de la création du project',
            'La date de fin du projet ne peut pas avoir lieu dans le passé.',
            context);
        return;
      }
    }

    final String url = '${dotenv.env['API_BASE_URL']}/projects';

    final Map<String, dynamic> requestBody = {
      "name": projectName,
      "description": description,
      "beginningDate": beginningDate,
      "endingDate": endingDate,
      "organizerEmail": user.email!
    };

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 201) {
        if (mounted) {
          Navigator.pop(context);
        }
      } else if (response.statusCode == 409) {
        Utils.errorMess('Erreur lors de la création du project',
            'Un projet avec ce nom existe déjà.', context);
      } else {
        Utils.errorMess(
            'Erreur lors de la création du project',
            'Erreur lors de la création du projet. Merci de réessayez plus tard.',
            context);
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de la création du project',
          'Impossible de se connecter au serveur.', context);
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
      body: Center(
          child: SingleChildScrollView(
              child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Text(
            'Initialiser un projet',
            style: TextStyle(
              fontSize: 30,
            ),
          ),
          const SizedBox(height: 25),
          TextFieldcustom(
            labelText: 'Nom du projet*',
            controller: projectNameController,
            obscureText: false,
            keyboardType: TextInputType.text,
          ),
          const SizedBox(height: 25),
          SizedBox(
            width: 250,
            child: TextField(
              controller: descriptionController,
              maxLines: null,
              minLines: 2,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'Description',
                fillColor: Color(0xFFF2F2F2),
                filled: true,
              ),
            ),
          ),
          const SizedBox(height: 25),
          SizedBox(
            width: 250,
            child: GestureDetector(
              onTap: () => Utils.selectDate(context, beginningDateController, _selectedBeginningDate,
                          (pickedDate) {
                        setState(() {
                          _selectedBeginningDate = pickedDate;
                        });
                      }),
              child: AbsorbPointer(
                child: TextField(
                  controller: beginningDateController,
                  readOnly: true,
                  decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Date de début',
                    fillColor: Color(0xFFF2F2F2),
                    filled: true,
                    prefixIcon: Icon(Icons.calendar_today),
                  ),
                ),
              ),
            ),
          ),
          const SizedBox(height: 25),
          SizedBox(
            width: 250,
            child: GestureDetector(
              onTap: () => Utils.selectDate(context, endingDateController, _selectedEndingDate,
                          (pickedDate) {
                        setState(() {
                          _selectedEndingDate = pickedDate;
                        });
                      }),
              child: AbsorbPointer(
                child: TextField(
                  controller: endingDateController,
                  readOnly: true,
                  decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Date de fin',
                    fillColor: Color(0xFFF2F2F2),
                    filled: true,
                    prefixIcon: Icon(Icons.calendar_today),
                  ),
                ),
              ),
            ),
          ),
          const SizedBox(height: 25),
          ButtonCustom(
            text: 'Enregistrer',
            onTap: () => save(context),
          ),
        ],
      ))),
    );
  }
}
