import 'package:calendar_app/auth/auth.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/textfield_custom.dart';

class NewProjectPage extends StatefulWidget {
  NewProjectPage({super.key});

  @override
  State<NewProjectPage> createState() => _NewProjectPageState();
}

class _NewProjectPageState extends State<NewProjectPage> {
  final user = FirebaseAuth.instance.currentUser!;

  final projectNameController = TextEditingController();

  final descriptionController = TextEditingController();

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

  void save(BuildContext context) {
    final projectName = projectNameController.text;
    final description = descriptionController.text;

    if (projectName.isEmpty) {
      errorMess('Veuillez donner un nom au project.');
      return;
    }

    // Example save logic (e.g., sending data to a database)
    // After saving, navigate back to the previous page
    Navigator.pop(
        context, {'projectName': projectName, 'description': description});
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
          ButtonCustom(
            text: 'Enregistrer',
            onTap: () => save(context),
          ),
        ],
      ))),
    );
  }
}
