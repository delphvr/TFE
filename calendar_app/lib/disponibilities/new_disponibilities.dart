import 'package:calendar_app/components/bottom_sheet_selector.dart';
import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class NewDisponibilitiesPage extends StatefulWidget {
  final Function onpop;
  const NewDisponibilitiesPage({
    super.key,
    required this.onpop,
  });

  @override
  State<NewDisponibilitiesPage> createState() => _NewDisponibilitiesPageSate();
}

class _NewDisponibilitiesPageSate extends State<NewDisponibilitiesPage> {
  final user = FirebaseAuth.instance.currentUser!;
  final String errorTitle = 'Erreur lors de l\'ajout des disponibilités';

  final startTimeController = TextEditingController();
  final endTimeController = TextEditingController();
  List<String> selectedDays = [];
  TimeOfDay? selectedStartTime;
  TimeOfDay? selectedEndTime;

  void save(BuildContext context) async {
    final email = user.email;
    final startTime = startTimeController.text;
    final endTime = endTimeController.text;

    if (startTime.isEmpty || endTime.isEmpty || selectedDays.isEmpty) {
      Utils.errorMess(errorTitle, 'Merci de remplir tout les champs', context);
      return;
    }

    Map<String, int> weekdays = {
      "Lundi": 0,
      "Mardi": 1,
      "Mercredi": 2,
      "Jeudi": 3,
      "Vendredi": 4,
      "Samedi": 5,
      "Dimanche": 6
    };

    List<int> weekdaysSelected = [];
    for (String day in selectedDays) {
      weekdaysSelected.add(weekdays[day]!);
    }

    final String url = '${dotenv.env['API_BASE_URL']}/availabilities';

    final Map<String, dynamic> requestBody = {
      "email": email,
      "startTime": startTime,
      "endTime": endTime,
      "weekdays": weekdaysSelected,
    };
    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 201) {
        widget.onpop();
        if (context.mounted) {
          Navigator.pop(context);
        }
      } else if (response.statusCode == 400){
        if (context.mounted) {
          Utils.errorMess(
              errorTitle,
              'Les disponibilités ne peuvent pas se superposer.',
              context);
        }
      } 
      else {
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
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  'Modification du Compte',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 30,
                  ),
                ),
                const SizedBox(height: 20),
                SizedBox(
                  width: 250,
                  child: TextField(
                    controller: startTimeController,
                    readOnly: true,
                    decoration: InputDecoration(
                      border: const OutlineInputBorder(),
                      labelText: 'Heure de début*',
                      fillColor: const Color(0xFFF2F2F2),
                      filled: true,
                      prefixIcon: const Icon(Icons.access_time),
                      suffixIcon: startTimeController.text.isNotEmpty
                          ? IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                setState(() {
                                  selectedStartTime = null;
                                  startTimeController.clear();
                                });
                              },
                            )
                          : null,
                    ),
                    onTap: () => Utils.selectTime(
                      context,
                      startTimeController,
                      selectedStartTime,
                      (TimeOfDay time) {
                        setState(() {
                          selectedStartTime = time;
                        });
                      },
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                SizedBox(
                  width: 250,
                  child: TextField(
                    controller: endTimeController,
                    readOnly: true,
                    decoration: InputDecoration(
                      border: const OutlineInputBorder(),
                      labelText: 'Heure de fin*',
                      fillColor: const Color(0xFFF2F2F2),
                      filled: true,
                      prefixIcon: const Icon(Icons.access_time),
                      suffixIcon: endTimeController.text.isNotEmpty
                          ? IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                setState(() {
                                  selectedEndTime = null;
                                  endTimeController.clear();
                                });
                              },
                            )
                          : null,
                    ),
                    onTap: () => Utils.selectTime(
                      context,
                      endTimeController,
                      selectedEndTime,
                      (TimeOfDay time) {
                        setState(() {
                          selectedEndTime = time;
                        });
                      },
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                BottomSheetSelector<String>(
                  items: const [
                    "Lundi",
                    "Mardi",
                    "Mercredi",
                    "Jeudi",
                    "Vendredi",
                    "Samedi",
                    "Dimanche"
                  ],
                  selectedItems: selectedDays,
                  onSelectionChanged: (selectedList) {
                    setState(() {
                      selectedDays = selectedList;
                    });
                  },
                  title: "Sélectionnez le/les jour(s)*",
                  buttonLabel: "Valider",
                  itemLabel: (day) => day,
                  textfield: "Jours",
                ),
                const SizedBox(height: 20),
                ButtonCustom(
                  text: 'Ajouter',
                  onTap: () => save(context),
                ),
                const SizedBox(height: 20),
              ],
            ),
          ),
        ),
        selectedIndex: 3);
  }
}
