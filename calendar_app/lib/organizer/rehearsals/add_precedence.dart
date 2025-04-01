import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';


class AddPrecedencePage extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final List<Map<String, dynamic>> rehearsals;
  final String rehearsalName;

  const AddPrecedencePage(
      {super.key, required this.projectId, required this.rehearsalId, required this.rehearsals, required this.rehearsalName});

  @override
  State<AddPrecedencePage> createState() => _AddPrecedencePageState();
}

class _AddPrecedencePageState extends State<AddPrecedencePage> {
  final user = FirebaseAuth.instance.currentUser!;

  List<Map<String, dynamic>> selectedReherasals = [];


  void addRehearsalPrecedences(BuildContext context) async {
    final String url = '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/precedences';

    if (selectedReherasals.isEmpty) {
      return;
    }

  print(selectedReherasals);
    final List requestBody = selectedReherasals.map((item) => item['rehearsalId']).toList();

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
        print(response.body);
        print(response.statusCode);
        if (context.mounted) {
          Utils.errorMess('Erreur lors de l\'ajout de la précédence',
              'Merci de réessayez plus tard.', context);
        }
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Erreur lors de l\'ajout de la précédence',
            'Impossible de se connecter au serveur.', context);
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
                  'Ajout d\'une précédence pour la répétition ${widget.rehearsalName}',
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 27,
                  ),
                ),
                const SizedBox(height: 20),
                BottomSheetSelector<Map<String, dynamic>>(
                  items: widget.rehearsals,
                  selectedItems: selectedReherasals,
                  onSelectionChanged: (selectedList) {
                    setState(() {
                      selectedReherasals = selectedList;
                    });
                  },
                  title: "Sélectionnez les répétitions qui doivent avoir lieux avant ${widget.rehearsalName}",
                  buttonLabel: "Valider",
                  itemLabel: (item) => item['name'],
                  textfield: "Répétitions",
                ),
                const SizedBox(height: 20),
                ButtonCustom(
                  text: 'Ajouter',
                  onTap: () => addRehearsalPrecedences(context),
                ),
                const SizedBox(height: 10),
              ],
            ),
          ),
        ),
      ),
      selectedIndex: 1,
    );
  }
}
