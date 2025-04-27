import 'package:calendar_app/components/button_custom.dart';
import 'package:calendar_app/components/scaffold_custom.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/// Page to add a vacation, by giving it a strat and end date.
class NewVacationsPage extends StatefulWidget {
  final Function onpop;
  const NewVacationsPage({
    super.key,
    required this.onpop,
  });

  @override
  State<NewVacationsPage> createState() => _NewVacationsPageSate();
}

class _NewVacationsPageSate extends State<NewVacationsPage> {
  final user = FirebaseAuth.instance.currentUser!;
  final String errorTitle = 'Erreur lors de l\'ajout des vacances';

  final startDateController = TextEditingController();
  final endDateController = TextEditingController();
  DateTime? selectedStartDate;
  DateTime? selectedEndDate;

  /// Save to the backend the new user vacation.
  /// Check that all the field are completed and that the end date is not before the start date. And that the end date is not in the past.
  /// If an error occurs, an error message will be display.
  void save(BuildContext context) async {
    final email = user.email;
    final startDate = Utils.formatDateTime(selectedStartDate);
    final endDate = Utils.formatDateTime(selectedEndDate);

    if (startDate.isEmpty || endDate.isEmpty) {
      Utils.errorMess(errorTitle, 'Merci de remplir tout les champs', context);
      return;
    }

    DateTime endDatetime = DateTime.parse(endDate);
    DateTime today = DateTime.now();
    DateTime todayWithoutTime = DateTime(today.year, today.month, today.day);
    if (todayWithoutTime.isAfter(endDatetime)) {
      Utils.errorMess(errorTitle,
          'La date de fin ne peut pas avoir lieu dans le passé.', context);
      return;
    }

    if (DateTime.parse(startDate).isAfter(DateTime.parse(endDate))) {
      Utils.errorMess(
          errorTitle,
          'La date de fin ne peut pas avoir lieu avant la date de début.',
          context);
      return;
    }

    final String url = '${dotenv.env['API_BASE_URL']}/vacations';

    final Map<String, dynamic> requestBody = {
      "email": email,
      "startDate": startDate,
      "endDate": endDate,
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
      } else {
        if (context.mounted) {
          Utils.errorMess(
              errorTitle,
              'Erreur lors de l\'ajout. Merci de réessayez plus tard.',
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
                  'Ajout de vacances',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: 30,
                  ),
                ),
                const SizedBox(height: 20),
                SizedBox(
                  width: 250,
                  child: TextField(
                    controller: startDateController,
                    readOnly: true,
                    decoration: InputDecoration(
                      border: const OutlineInputBorder(),
                      labelText: 'Date de début*',
                      fillColor: const Color(0xFFF2F2F2),
                      filled: true,
                      prefixIcon: const Icon(Icons.calendar_today),
                      suffixIcon: startDateController.text.isNotEmpty
                          ? IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                setState(() {
                                  selectedStartDate = null;
                                  startDateController.clear();
                                });
                              },
                            )
                          : null,
                    ),
                    onTap: () => Utils.selectDate(
                      context,
                      startDateController,
                      selectedStartDate,
                      (pickedDate) {
                        setState(() {
                          selectedStartDate = pickedDate;
                        });
                      },
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                SizedBox(
                  width: 250,
                  child: TextField(
                    controller: endDateController,
                    readOnly: true,
                    decoration: InputDecoration(
                      border: const OutlineInputBorder(),
                      labelText: 'Date de fin*',
                      fillColor: const Color(0xFFF2F2F2),
                      filled: true,
                      prefixIcon: const Icon(Icons.calendar_today),
                      suffixIcon: endDateController.text.isNotEmpty
                          ? IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                setState(() {
                                  selectedEndDate = null;
                                  endDateController.clear();
                                });
                              },
                            )
                          : null,
                    ),
                    onTap: () => Utils.selectDate(
                      context,
                      endDateController,
                      selectedEndDate,
                      (pickedDate) {
                        setState(() {
                          selectedEndDate = pickedDate;
                        });
                      },
                    ),
                  ),
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
