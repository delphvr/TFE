import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class UpdateProjectPage extends StatefulWidget {
  final int id;
  final String name;
  final String? description;
  final String? beginningDate;
  final String? endingDate;

  const UpdateProjectPage({
    super.key,
    required this.id,
    required this.name,
    this.description,
    this.beginningDate,
    this.endingDate,
  });

  @override
  State<UpdateProjectPage> createState() => _UpdateProjectPageState();
}

class _UpdateProjectPageState extends State<UpdateProjectPage> {
  final user = FirebaseAuth.instance.currentUser!;

  late TextEditingController titleController;
  late TextEditingController descriptionController;
  late TextEditingController beginningDateController;
  late TextEditingController endingDateController;

  @override
  void initState() {
    super.initState();
    titleController = TextEditingController(text: widget.name);
    descriptionController = TextEditingController(text: widget.description);
    beginningDateController = TextEditingController(text: widget.beginningDate);
    endingDateController = TextEditingController(text: widget.endingDate);
  }

  @override
  void dispose() {
    titleController.dispose();
    descriptionController.dispose();
    beginningDateController.dispose();
    endingDateController.dispose();
    super.dispose();
  }

  DateTime? selectedDate;

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  void update(BuildContext context) async {
    final projectName = titleController.text;
    final description = descriptionController.text;
    final beginningDate = beginningDateController.text;
    final endingDate = endingDateController.text;

    if (projectName.isEmpty) {
      Utils.errorMess('Erreur lors de la modification du project',
          'Veuillez donner un nom au project.', context);
      return;
    }

    if (beginningDate.isNotEmpty && endingDate.isNotEmpty) {
      if (DateTime.parse(beginningDate).isAfter(DateTime.parse(endingDate))) {
        Utils.errorMess(
            'Erreur lors de la modification du project',
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
      final response = await http.put(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 200) {
        if (mounted) {
          Navigator.pop(context, {
            'name': projectName,
            'description': description,
            'beginningDate': beginningDate,
            'endingDate': endingDate,
          });
        }
      } else {
        print(response.body);
        Utils.errorMess(
            'Erreur lors de la modification du project',
            'Erreur lors de la modification du projet. Merci de réessayez plus tard.',
            context);
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de la modification du project',
          'Impossible de se connecter au serveur.', context);
    }
  }

  //Done with the help of chatgpt
  Future<void> selectDate(
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
              'Modification du projet ${widget.name}',
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 27,
              ),
            ),
            const SizedBox(height: 25),
            TextFieldcustom(
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
                onTap: () => selectDate(context, beginningDateController),
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
                onTap: () => selectDate(context, endingDateController),
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
              text: 'Sauvgarder',
              onTap: () {
                update(context);
              },
            ),
          ],
        ),
      ))),
    );
  }
}
