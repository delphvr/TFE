import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:multi_select_flutter/multi_select_flutter.dart';

class Participant {
  final String name;
  final int id;

  Participant({required this.name, required this.id});

  @override
  bool operator ==(Object other) =>
      identical(this, other) || (other is Participant && other.id == id);

  @override
  int get hashCode => id.hashCode;
}

class AddRehearsal extends StatefulWidget {
  final int projectId;
  final String projectName;

  const AddRehearsal(
      {super.key, required this.projectId, required this.projectName});

  @override
  State<AddRehearsal> createState() => _AddRehearsal();
}

class _AddRehearsal extends State<AddRehearsal> {
  final user = FirebaseAuth.instance.currentUser!;

  TextEditingController nameController = TextEditingController();
  TextEditingController descriptionController = TextEditingController();
  TextEditingController dateController = TextEditingController();
  TextEditingController durationController = TextEditingController();
  String isoDuration = '';
  DateTime? _selectedDate;
  List<Participant> participants = [];
  List<Participant> selectedParticipants = [];
  List<MultiSelectItem<Participant>> items = [];

  @override
  void initState() {
    super.initState();
    getUsersOnProject(
        context); //initiate participants list with the participants of the project
  }

  void addRehearsal(BuildContext context) async {
    final rehearsalName = nameController.text;
    final description = descriptionController.text;
    final date = Utils.formatDateTime(_selectedDate);
    final duration = isoDuration;
    final String url = '${dotenv.env['API_BASE_URL']}/rehearsals';

    //TODO check que la date rentre dans les dates du projet, le passer à select date?
    if (rehearsalName.isEmpty) {
      Utils.errorMess('Erreur lors de la création de la répétition',
          'Merci de donner un nom à la répétition', context);
      return;
    }
    if (date.isNotEmpty) {
      DateTime selectedDate = DateTime.parse(date);
      DateTime today = DateTime.now();
      DateTime todayWithoutTime = DateTime(today.year, today.month, today.day);
      if (todayWithoutTime.isAfter(selectedDate)) {
        Utils.errorMess('Erreur lors de la création de la répétition',
            'La date ne peut pas avoir lieu dans le passé.', context);
        return;
      }
    }

    final Map<String, dynamic> requestBody = {
      "name": rehearsalName,
      "description": description,
      "date": date,
      "duration": duration,
      "projectId": widget.projectId,
      "participantsIds": selectedParticipants.map((item) => item.id).toList()
    };

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 201) {
        if (context.mounted) {
          Navigator.pop(context);
        }
      } else {
        if (context.mounted) {
          Utils.errorMess('Erreur lors de l\'ajout du participant',
              'Merci de réessayez plus tard.', context);
        }
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de l\'ajout du participant',
            'Impossible de se connecter au serveur.', context);
      }
    }
  }

  Future<void> getUsersOnProject(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/userProjects/${widget.projectId}';
    try {
      final response = await http.get(Uri.parse(url));
      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          participants = data
              .map((item) => Participant(
                  name: "${item['firstName']} ${item['lastName']}",
                  id: item['id']))
              .toList();
          items = participants
              .map((participant) =>
                  MultiSelectItem<Participant>(participant, participant.name))
              .toList();
        });
      }
    } catch (e) {
      print("Error fetching users: $e");
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
                    'Ajout d\'une répétition au projet ${widget.projectName}',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 27,
                    ),
                  ),
                  const SizedBox(height: 20),
                  TextFieldcustom(
                    labelText: 'Nom de la répétition *',
                    controller: nameController,
                    obscureText: false,
                    keyboardType: TextInputType.text,
                  ),
                  const SizedBox(height: 20),
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
                  const SizedBox(height: 20),
                  SizedBox(
                    width: 250,
                    child: GestureDetector(
                      onTap: () => Utils.selectDate(
                          context, dateController, _selectedDate, (pickedDate) {
                        setState(() {
                          _selectedDate = pickedDate;
                        });
                      }),
                      child: AbsorbPointer(
                        child: TextField(
                          controller: dateController,
                          readOnly: true,
                          decoration: const InputDecoration(
                            border: OutlineInputBorder(),
                            labelText: 'Date',
                            fillColor: Color(0xFFF2F2F2),
                            filled: true,
                            prefixIcon: Icon(Icons.calendar_today),
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),
                  SizedBox(
                    width: 250,
                    child: GestureDetector(
                      onTap: () => Utils.selectDuration(
                        context,
                        durationController,
                        durationController.text,
                        (newDuration) {
                          setState(() {
                            isoDuration = newDuration;
                          });
                        },
                      ),
                      child: AbsorbPointer(
                        child: TextField(
                          controller: durationController,
                          readOnly: true,
                          decoration: const InputDecoration(
                            border: OutlineInputBorder(),
                            labelText: 'Durée',
                            fillColor: Color(0xFFF2F2F2),
                            filled: true,
                            prefixIcon: Icon(Icons.timer),
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),
                  BottomSheetSelector<Participant>(
                    items: participants,
                    selectedItems: selectedParticipants,
                    onSelectionChanged: (selectedList) {
                      setState(() {
                        selectedParticipants = selectedList;
                      });
                    },
                    title: "Sélectionnez les participants",
                    buttonLabel: "Valider",
                    itemLabel: (role) => role.name,
                    textfield: "Participants",
                  ),
                  const SizedBox(height: 20),
                  ButtonCustom(
                    text: 'Ajouter',
                    onTap: () => addRehearsal(context),
                  ),
                  const SizedBox(height: 10),
                ],
              ),
            ),
          ),
        ),
        selectedIndex: 1);
  }
}
