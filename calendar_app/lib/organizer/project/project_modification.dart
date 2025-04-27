import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Page to update the informations about a project (name, description (optional field), start date and end date).
class UpdateProjectPage extends StatefulWidget {
  final int id;
  final String name;
  final String? description;
  final String? beginningDate;
  final String? endingDate;
  final http.Client? client;
  final FirebaseAuth? auth;

  const UpdateProjectPage({
    super.key,
    required this.id,
    required this.name,
    this.description,
    this.beginningDate,
    this.endingDate,
    this.client,
    this.auth,
  });

  @override
  State<UpdateProjectPage> createState() => _UpdateProjectPageState();
}

class _UpdateProjectPageState extends State<UpdateProjectPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  final String errorTitle = 'Erreur lors de la modification du project';

  late TextEditingController titleController;
  late TextEditingController descriptionController;
  late TextEditingController beginningDateController;
  late TextEditingController endingDateController;
  DateTime? _selectedBeginningDate;
  DateTime? _selectedEndingDate;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    titleController = TextEditingController(text: widget.name);
    descriptionController = TextEditingController(text: widget.description);
    beginningDateController = TextEditingController(
        text: widget.beginningDate != null
            ? Utils.formatDateString(widget.beginningDate)
            : "");
    endingDateController = TextEditingController(
        text: widget.endingDate != null
            ? Utils.formatDateString(widget.endingDate)
            : "");
    if (widget.beginningDate != null) {
      _selectedBeginningDate = DateTime.parse(widget.beginningDate!);
    }

    if (widget.endingDate != null) {
      _selectedEndingDate = DateTime.parse(widget.endingDate!);
    }
  }

  @override
  void dispose() {
    titleController.dispose();
    descriptionController.dispose();
    beginningDateController.dispose();
    endingDateController.dispose();
    super.dispose();
  }

  /// Send to the backend the updated information about the project with id [widget.id] to be saved
  /// Check that the name and dates of the project are not empty. And check that the end dateis not before the start date.
  /// If an error occurs an error message will be display.
  void update(BuildContext context) async {
    final projectName = titleController.text;
    final description = descriptionController.text;
    final beginningDate = Utils.formatDateTime(_selectedBeginningDate);
    final endingDate = Utils.formatDateTime(_selectedEndingDate);

    if (projectName.isEmpty) {
      Utils.errorMess(
          errorTitle, 'Veuillez donner un nom au project.', context);
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
            errorTitle,
            'La date de fin du projet ne peut pas avoir lieu avant la date de début.',
            context);
        return;
      }
    }

    final String url = '${dotenv.env['API_BASE_URL']}/projects/${widget.id}';

    final Map<String, dynamic> requestBody = {
      "name": projectName,
      "description": description,
      "beginningDate": beginningDate,
      "endingDate": endingDate,
    };

    try {
      final response = await client.put(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 200) {
        if (context.mounted) {
          Navigator.pop(context, {
            'name': projectName,
            'description': description,
            'beginningDate': beginningDate,
            'endingDate': endingDate,
          });
        }
      } else {
        if (context.mounted) {
          Utils.errorMess(
              errorTitle,
              'Erreur lors de la modification du projet. Merci de réessayez plus tard.',
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
                child: Padding(
          padding: const EdgeInsets.all(25.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Modification du projet ${widget.name}',
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 27,
                ),
              ),
              const SizedBox(height: 25),
              TextFieldcustom(
                key: const Key('nameField'),
                labelText: 'Nom du projet*',
                controller: titleController,
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
                  onTap: () => Utils.selectDate(
                      context, beginningDateController, _selectedBeginningDate,
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
                  onTap: () => Utils.selectDate(
                      context, endingDateController, _selectedEndingDate,
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
                text: 'Sauvegarder',
                onTap: () {
                  update(context);
                },
              ),
            ],
          ),
        ))),
        selectedIndex: 1);
  }
}
