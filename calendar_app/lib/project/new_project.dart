import 'package:calendar_app/auth/auth.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class NewProjectPage extends StatefulWidget {
  NewProjectPage({super.key});

  @override
  State<NewProjectPage> createState() => _NewProjectPageState();
}

class _NewProjectPageState extends State<NewProjectPage> {
  final user = FirebaseAuth.instance.currentUser!;

  final projectNameController = TextEditingController();
  final descriptionController = TextEditingController();
  final beginningDateController = TextEditingController();
  final endingDateController = TextEditingController();

  DateTime? _selectedDate;

  void logout(Function onLogoutSuccess) async {
    await FirebaseAuth.instance.signOut();
    onLogoutSuccess();
  }

  //TODO: mettre ça dans function auxilliaire?
  void errorMess(String message) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
            title: const Text('Erreur lors de la création du project'),
            content: Text(message),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                },
                child: const Text('OK'),
              ),
            ]);
      },
    );
  }

  void save(BuildContext context) async {
    final projectName = projectNameController.text;
    final description = descriptionController.text;
    final beginningDate = beginningDateController.text;
    final endingDate = endingDateController.text;

    if (projectName.isEmpty) {
      errorMess('Veuillez donner un nom au project.');
      return;
    }

    final String apiUrl = '${dotenv.env['API_BASE_URL']}/projects';

    final Map<String, dynamic> requestBody = {
      "name": projectName,
      "description": description,
      "beginningDate": beginningDate,
      "endingDate": endingDate,
      "organizerEmail": user.email!
    };

    try {
      // Send POST request
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 201) {
        print("statusCode: 201");
        if (mounted) {
          Navigator.pop(context);
        }
      } else if (response.statusCode == 409) {
        errorMess('Un projet avec ce nom existe déjà.');
      } else {
        errorMess(
            'Erreur lors de la création du projet. Merci de réessayez plus tard.');
      }
    } catch (e) {
      errorMess('Impossible de se connecter au serveur.');
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
            labelText: 'Nom du projet',
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
            child: TextField(
              controller: beginningDateController,
              readOnly: true,
              decoration: InputDecoration(
                border: const OutlineInputBorder(),
                labelText: 'Date de début',
                fillColor: const Color(0xFFF2F2F2),
                filled: true,
                prefixIcon: IconButton(
                  icon: const Icon(Icons.calendar_today),
                  onPressed: () =>
                      _selectDate(context, beginningDateController),
                ),
              ),
            ),
          ),
          const SizedBox(height: 25),
          SizedBox(
            width: 250,
            child: TextField(
              controller: endingDateController,
              readOnly: true,
              decoration: InputDecoration(
                border: const OutlineInputBorder(),
                labelText: 'Date de fin',
                fillColor: const Color(0xFFF2F2F2),
                filled: true,
                prefixIcon: IconButton(
                  icon: const Icon(Icons.calendar_today),
                  onPressed: () => _selectDate(context, endingDateController),
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
