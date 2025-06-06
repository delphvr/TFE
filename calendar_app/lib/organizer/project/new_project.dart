import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';


/// page to create a new project
/// Ask for project name, description (optional), start date, end date.
class NewProjectPage extends StatefulWidget {
  final FirebaseAuth? auth;
  final http.Client? client;
  const NewProjectPage({
    super.key,
    this.auth,
    this.client,
  });

  @override
  State<NewProjectPage> createState() => _NewProjectPageState();
}

class _NewProjectPageState extends State<NewProjectPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;

  final projectNameController = TextEditingController();
  final descriptionController = TextEditingController();
  final beginningDateController = TextEditingController();
  final endingDateController = TextEditingController();

  DateTime? _selectedBeginningDate;
  DateTime? _selectedEndingDate;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
  }

  /// save the new project in the backend. 
  /// Check that the project name, starting date and endig date is set. If not an error message is displayed.
  /// If an error occurs an error message is displayed.
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

    if (beginningDate.isEmpty){
      Utils.errorMess('Erreur lors de la création du project',
          'Veuillez donner une date de début au project.', context);
      return;
    }
    if(endingDate.isEmpty){
      Utils.errorMess('Erreur lors de la création du project',
          'Veuillez donner une date de fin au project.', context);
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
      DateTime today = DateTime.now();
      DateTime todayWithoutTime = DateTime(today.year, today.month, today.day);
      if (todayWithoutTime.isAfter(DateTime.parse(endingDate))) {
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
      final response = await client.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 201) {
        if (context.mounted) {
          Navigator.pop(context);
        }
      } else if (response.statusCode == 409) {
        if (context.mounted) {
          Utils.errorMess('Erreur lors de la création du project',
              'Un projet avec ce nom existe déjà.', context);
        }
      } else {
        if (context.mounted) {
          Utils.errorMess(
              'Erreur lors de la création du project',
              'Erreur lors de la création du projet. Merci de réessayez plus tard.',
              context);
        }
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de la création du project',
            'Impossible de se connecter au serveur.', context);
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
              'Initialiser un projet',
              style: TextStyle(
                fontSize: 30,
              ),
            ),
            const SizedBox(height: 25),
            TextFieldcustom(
              key: const Key('name'),
              labelText: 'Nom du projet*',
              controller: projectNameController,
              obscureText: false,
              keyboardType: TextInputType.text,
            ),
            const SizedBox(height: 25),
            SizedBox(
              width: 250,
              child: TextField(
                key: const Key('description'),
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
                onTap: () => Utils.selectDate(
                    context, beginningDateController, _selectedBeginningDate,
                    (pickedDate) {
                  setState(() {
                    _selectedBeginningDate = pickedDate;
                  });
                }),
                child: AbsorbPointer(
                  key: const Key('beginningDate'),
                  child: TextField(
                    controller: beginningDateController,
                    readOnly: true,
                    decoration: const InputDecoration(
                      border: OutlineInputBorder(),
                      labelText: 'Date de début*',
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
                onTap: () => Utils.selectDate(
                    context, endingDateController, _selectedEndingDate,
                    (pickedDate) {
                  setState(() {
                    _selectedEndingDate = pickedDate;
                  });
                }),
                child: AbsorbPointer(
                  key: const Key('endingDate'),
                  child: TextField(
                    controller: endingDateController,
                    readOnly: true,
                    decoration: const InputDecoration(
                      border: OutlineInputBorder(),
                      labelText: 'Date de fin*',
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
        selectedIndex: 1);
  }
}
