import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

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
  DateTime? _selectedDate;

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  void addRehearsal(BuildContext context) async {
    final rehearsalName = nameController.text;
    final description = descriptionController.text;
    final date = dateController.text;
    final duration = durationController.text; //TODO
    final String url = '${dotenv.env['API_BASE_URL']}/rehearsals';

    //TODO check que la date rentre dans la date du projet

    final Map<String, dynamic> requestBody = {
      "name": rehearsalName, 
    "description": description,
    "date": date,
    "duration": duration,
    "projectId": widget.projectId,
    "participantsIds": []
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
      } else {
        Utils.errorMess('Erreur lors de l\'ajout du participant',
            'Merci de réessayez plus tard.', context);
      }
    } catch (e) {
      Utils.errorMess('Erreur lors de l\'ajout du participant',
          'Impossible de se connecter au serveur.', context);
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
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
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
                      onTap: () =>
                          _selectDate(context, dateController),
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
                  const SizedBox(height: 25),
                  ButtonCustom(
                    text: 'Ajouter',
                    onTap: () => addRehearsal(context),
                  ),
                  const SizedBox(height: 10),
                ],
              ),
            ),
          ),
        ));
  }
}
