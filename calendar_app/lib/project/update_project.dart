import 'package:calendar_app/auth/auth.dart';
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

  late TextEditingController titreController;

  late TextEditingController descriptionController;
  late TextEditingController beginningDateController;
  late TextEditingController endingDateController;

  @override
  void initState() {
    super.initState();
    titreController = TextEditingController(text: widget.name);
    descriptionController = TextEditingController(text: widget.description);
    beginningDateController = TextEditingController(text: widget.beginningDate);
    endingDateController = TextEditingController(text: widget.endingDate);
  }

  @override
  void dispose() {
    titreController.dispose();
    descriptionController.dispose();
    beginningDateController.dispose();
    endingDateController.dispose();
    super.dispose();
  }

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
            title: const Text('Erreur lors de la modification du project'),
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

  void update(BuildContext context) async {
    if (mounted) {
      Navigator.pop(context);
    }
  }

  //Done with the help of chatgpt
  //TODO mettre dans un fichier utils ?
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
              'Modification du projet ${widget.name}',
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 30,
              ),
            ),
            const SizedBox(height: 25),
            TextFieldcustom(
              labelText: 'Nom du projet*',
              controller: titreController,
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
                onTap: () => _selectDate(context, beginningDateController),
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
                onTap: () => _selectDate(context, endingDateController),
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
