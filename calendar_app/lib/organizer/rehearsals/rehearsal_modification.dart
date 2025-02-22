import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/organizer/rehearsals/add_rehearsal.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:multi_select_flutter/multi_select_flutter.dart';


class RehearsalModificationPage extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final String name;
  final String? description;
  final String? date;
  final String? duration;
  final List participantsIds;

  const RehearsalModificationPage({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.name,
    required this.description,
    required this.date,
    required this.duration,
    required this.participantsIds,
  });

  @override
  State<RehearsalModificationPage> createState() =>
      _RehearsalModificationPage();
}

class _RehearsalModificationPage extends State<RehearsalModificationPage> {
  final user = FirebaseAuth.instance.currentUser!;
  final String errorTitle = 'Erreur lors de la modification de la répétition';

  late TextEditingController nameController;
  late TextEditingController descriptionController;
  late TextEditingController dateController;
  late TextEditingController durationController;
  String isoDuration = '';
  List<Participant> participants = [];
  List<Participant> selectedParticipants = [];
  List<MultiSelectItem<Participant>> items = [];

  @override
  void initState() {
    super.initState();
    nameController = TextEditingController(text: widget.name);
    descriptionController = TextEditingController(text: widget.description);
    dateController = TextEditingController(text: widget.date);
    isoDuration = widget.duration !=null ? widget.duration! : '' ;
    durationController = TextEditingController(text: widget.duration !=null ? Utils.formatDuration(widget.duration!) : null);
    getUsersOnProject(
        context); //initiate participants list with the participants of the project
    //init selectedParticipants to user already participating in the rehearsal
    getUsersOnRehearsal(context);
  }

  @override
  void dispose() {
    nameController.dispose();
    descriptionController.dispose();
    dateController.dispose();
    durationController.dispose();
    super.dispose();
  }

  DateTime? selectedDate;

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
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

  void getUsersOnRehearsal(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/participants';
    try {
      final response = await http.get(Uri.parse(url));

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
      print(e);
    }
  }

  void update(BuildContext context) async {
    final rehearsalName = nameController.text;
    final description = descriptionController.text;
    final date = dateController.text;
    final duration = isoDuration;

    if (rehearsalName.isEmpty) {
      Utils.errorMess(
          errorTitle, 'Veuillez donner un nom à la répétition.', context);
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
        if (mounted) {
          Navigator.pop(context);
        }
      } else {
        Utils.errorMess(
            errorTitle,
            'Erreur lors de la modification. Merci de réessayez plus tard.',
            context);
      }
    } catch (e) {
      Utils.errorMess(
          errorTitle, 'Impossible de se connecter au serveur.', context);
    }
  }

  //Done with the help of chatgpt
  Future<void> _selectDate(
      BuildContext context, TextEditingController controller) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2101),
    );
    if (picked != null && picked != selectedDate) {
      setState(() {
        selectedDate = picked;
        controller.text =
            "${picked.toLocal()}".split(' ')[0]; // Format as yyyy-MM-dd
      });
    }
  }

  //Done with the help of chatgpt
  Future<void> _selectDuration(BuildContext context) async {
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: const TimeOfDay(hour: 0, minute: 0),
    );
    if (picked != null) {
      setState(() {
        // Store ISO-8601 format for backend
        isoDuration = "PT${picked.hour}H${picked.minute}M";
        // Display format (e.g., "2h 30m")
        String displayDuration = "${picked.hour}h ${picked.minute}m";
        durationController.text = displayDuration;
      });
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
                      onTap: () => _selectDate(context, dateController),
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
                      onTap: () =>
                          _selectDuration(context), 
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
        ));
  }
}
