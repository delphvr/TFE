import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/organizer/rehearsals/add_rehearsal.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:multi_select_flutter/multi_select_flutter.dart';

/// Page to update a rehearsal informations (name, description, date, time, duration, place and participants)
class RehearsalModificationPage extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final String name;
  final String? description;
  final String? date;
  final String? time;
  final String? duration;
  final List participantsIds;
  final String? location;
  final http.Client? client;
  final FirebaseAuth? auth;

  const RehearsalModificationPage({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.name,
    required this.description,
    required this.date,
    required this.time,
    required this.duration,
    required this.participantsIds,
    required this.location,
    this.client,
    this.auth,
  });

  @override
  State<RehearsalModificationPage> createState() =>
      _RehearsalModificationPage();
}

class _RehearsalModificationPage extends State<RehearsalModificationPage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  final String errorTitle = 'Erreur lors de la modification de la répétition';

  late TextEditingController nameController;
  late TextEditingController descriptionController;
  late TextEditingController dateController;
  late TextEditingController timeController;
  late TextEditingController durationController;
  late TextEditingController locationController;
  String isoDuration = '';
  List<Participant> participants = [];
  List<Participant> selectedParticipants = [];
  List<MultiSelectItem<Participant>> items = [];
  DateTime? selectedDate;
  TimeOfDay? _selectedTime;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    nameController = TextEditingController(text: widget.name);
    descriptionController = TextEditingController(text: widget.description);
    dateController = TextEditingController(
        text: widget.date != null ? Utils.formatDateString(widget.date) : "");
    if (widget.date != null) {
      selectedDate = DateTime.parse(widget.date!);
    }
    locationController = TextEditingController(text: widget.location);
    isoDuration = widget.duration != null ? widget.duration! : '';
    durationController = TextEditingController(
        text: widget.duration != null
            ? Utils.formatDuration(widget.duration!)
            : null);
    timeController = TextEditingController(text: widget.time);
    getUsersOnProject(context);
    getUsersOnRehearsal(context);
  }

  @override
  void dispose() {
    nameController.dispose();
    descriptionController.dispose();
    dateController.dispose();
    durationController.dispose();
    locationController.dispose();
    super.dispose();
  }

  /// Update the variables [participants] and [items] with the list of participants of the project taken from the backend.
  Future<void> getUsersOnProject(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/userProjects/${widget.projectId}';
    try {
      final response = await client.get(Uri.parse(url));
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
      return;
    }
  }

  /// update the variable [selectedParticipants] with the list of user already participating in the rehearsal. Information retreived by the backend.
  void getUsersOnRehearsal(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/participants';
    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          selectedParticipants = data
              .map((item) => Participant(
                  name: "${item['firstName']} ${item['lastName']}",
                  id: item['id']))
              .toList();
        });
      }
    } catch (e) {
      return;
    }
  }

  /// Send the updated information to the backend to be saved.
  /// Check that the name and duration is not empty.
  /// If an error occurs an error message will be displayed.
  void update(BuildContext context) async {
    final rehearsalName = nameController.text;
    final description = descriptionController.text;
    final date = Utils.formatDateTime(selectedDate);
    final time = timeController.text;
    final duration = isoDuration;

    if (rehearsalName.isEmpty) {
      Utils.errorMess(
          errorTitle, 'Veuillez donner un nom à la répétition.', context);
      return;
    }
    if (duration.isEmpty) {
      Utils.errorMess('Erreur lors de la création de la répétition',
          'Merci de donner une durée à la répétition', context);
      return;
    }

    if (date.isNotEmpty) {
      //TODO check que la date est comprise dans les dates du projet
    }

    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}';

    final Map<String, dynamic> requestBody = {
      "name": rehearsalName,
      "description": description,
      "date": date,
      "time": time,
      "duration": duration,
      "participantsIds": selectedParticipants.map((item) => item.id).toList(),
      "projectId": widget.projectId
    };

    try {
      final response = await http.put(
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
            child: Padding(
              padding: const EdgeInsets.all(25.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    'Modification de la répétition ${widget.name}',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 27,
                    ),
                  ),
                  const SizedBox(height: 20),
                  TextFieldcustom(
                    key: const Key('nameField'),
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
                    child: TextField(
                      controller: dateController,
                      readOnly: true,
                      decoration: InputDecoration(
                        border: const OutlineInputBorder(),
                        labelText: 'Date',
                        fillColor: const Color(0xFFF2F2F2),
                        filled: true,
                        prefixIcon: const Icon(Icons.calendar_today),
                        suffixIcon: dateController.text.isNotEmpty
                            ? IconButton(
                                icon: const Icon(Icons.clear),
                                onPressed: () {
                                  setState(() {
                                    selectedDate = null;
                                    dateController.clear();
                                  });
                                },
                              )
                            : null,
                      ),
                      onTap: () => Utils.selectDate(
                        context,
                        dateController,
                        selectedDate,
                        (pickedDate) {
                          setState(() {
                            selectedDate = pickedDate;
                          });
                        },
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),
                  SizedBox(
                    width: 250,
                    child: TextField(
                      controller: timeController,
                      readOnly: true,
                      decoration: InputDecoration(
                        border: const OutlineInputBorder(),
                        labelText: 'Heure',
                        fillColor: const Color(0xFFF2F2F2),
                        filled: true,
                        prefixIcon: const Icon(Icons.access_time),
                        suffixIcon: timeController.text.isNotEmpty
                            ? IconButton(
                                icon: const Icon(Icons.clear),
                                onPressed: () {
                                  setState(() {
                                    _selectedTime = null;
                                    timeController.clear();
                                  });
                                },
                              )
                            : null,
                      ),
                      onTap: () => Utils.selectTime(
                        context,
                        timeController,
                        _selectedTime,
                        (TimeOfDay time) {
                          setState(() {
                            _selectedTime = time;
                          });
                        },
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
                        child: TextFieldcustom(
                          labelText: 'Durée *',
                          controller: durationController,
                          obscureText: false,
                          keyboardType: TextInputType.text,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),
                  TextFieldcustom(
                    labelText: 'Lieu',
                    controller: locationController,
                    obscureText: false,
                    keyboardType: TextInputType.text,
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
                    text: 'Modifier',
                    onTap: () {
                      update(context);
                    },
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
