import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';

/// Page to add a rehearsal precedence to the rehearsal with id [rehearsalId] and a rehearsal in [rehearsals].
class AddPrecedencePage extends StatefulWidget {
  final int projectId;
  final int rehearsalId;
  final List<Map<String, dynamic>> rehearsals;
  final String rehearsalName;
  final http.Client? client;
  final FirebaseAuth? auth;

  const AddPrecedencePage({
    super.key,
    required this.projectId,
    required this.rehearsalId,
    required this.rehearsals,
    required this.rehearsalName,
    this.client,
    this.auth,
  });

  @override
  State<AddPrecedencePage> createState() => _AddPrecedencePageState();
}

class _AddPrecedencePageState extends State<AddPrecedencePage> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  final String errorTitle = 'Erreur lors de l\'ajout de la précédence';

  List<Map<String, dynamic>> selectedReherasals = [];

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
  }

  /// Add the rehearsal precedence statingt that the [selectedReherasals] needs to happend before the rehearsal with id [widget.rehearsalId].
  /// If an error occurs, an error message will be displayed.
  void addRehearsalPrecedences(BuildContext context) async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/${widget.rehearsalId}/precedences';

    if (selectedReherasals.isEmpty) {
      return;
    }

    final List requestBody =
        selectedReherasals.map((item) => item['rehearsalId']).toList();

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
          Utils.errorMess(errorTitle, 'Merci de réessayez plus tard.', context);
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
                  title:
                      "Sélectionnez les répétitions qui doivent avoir lieux avant ${widget.rehearsalName}",
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
