import 'package:calendar_app/auth/auth.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

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

  @override
  void initState() {
    super.initState();
    nameController = TextEditingController(text: widget.name);
    descriptionController = TextEditingController(text: widget.description);
    dateController = TextEditingController(text: widget.date);
    durationController = TextEditingController(text: widget.duration);
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

  void update(BuildContext context) async {
    final rehearsalName = nameController.text;
    final description = descriptionController.text;
    final date = dateController.text;
    final duration = durationController.text;

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
            'name': rehearsalName,
            'description': description,
            'date': date,
            'duration': duration,
          });
        }
      } else {
        print(response.body);
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
      body: Align(
        alignment: Alignment.topCenter,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Text(
              widget.name,
              textAlign: TextAlign.center,
              style: const TextStyle(
                fontSize: 30,
              ),
            ),
            const SizedBox(height: 25),
            const SizedBox(height: 10),
            const SizedBox(height: 25),
          ],
        ),
      ),
    );
  }
}
