import 'package:flutter/material.dart';

class Utils {
  static void errorMess(String title, String message, BuildContext context) {
    print("5");
    print(title);
    print(message);
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
            title: Text(title),
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

  static String? formatDateString(String? date) {
    if (date != null) {
      List<String> parts = date.split('-');
      if (parts.length == 3) {
        return "${parts[2]}-${parts[1]}-${parts[0]}";
      }
      return date;
    }
    return date;
  }

  static String formatDateTime(DateTime? date) {
    if (date == null) {
      return "";
    }
    return "${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}";
  }

  //Source: https://stackoverflow.com/questions/16800540/how-should-i-check-if-the-input-is-an-email-address-in-flutter
  static bool isValidEmail(String email) {
    return RegExp(
            r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$')
        .hasMatch(email);
  }

  static void confirmation(
      String title, String message, Function act, BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
            title: Text(title),
            content: Text(message),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                },
                child: const Text('annuler'),
              ),
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                  act();
                },
                child: const Text('oui'),
              ),
            ]);
      },
    );
  }

  //Done with chatgpt
  static String formatDuration(String duration) {
    final regex = RegExp(r'PT(?:(\d+)H)?(?:(\d+)M)?');
    final match = regex.firstMatch(duration);

    if (match == null) return duration;

    final hours = match.group(1);
    final minutes = match.group(2);

    String formatted = '';
    if (hours != null) formatted += '${hours}h';
    if (minutes != null) formatted += minutes;

    return formatted;
  }

  //Done with the help of chatgpt
  static Future<void> selectDate(
      BuildContext context,
      TextEditingController controller,
      DateTime? selectedDate,
      Function(DateTime) onDateSelected) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate ?? DateTime.now(),
      firstDate: DateTime.now().subtract(const Duration(days: 50 * 365)),
      lastDate: DateTime.now().add(const Duration(days: 50 * 365)),
    );

    if (picked != null) {
      controller.text =
          "${picked.day.toString().padLeft(2, '0')}-${picked.month.toString().padLeft(2, '0')}-${picked.year}";
      onDateSelected(picked);
    }
  }

  //Done with the help of chatgpt
  static Future<void> selectDuration(
      BuildContext context,
      TextEditingController controller,
      String? selectedDuration,
      Function(String) updateDuration) async {
    int initialHours = 0;
    int initialMinutes = 0;

    if (selectedDuration != null) {
      selectedDuration = formatDuration(selectedDuration);
    }
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: TimeOfDay(hour: initialHours, minute: initialMinutes),
    );
    if (picked != null) {
      // Format the ISO duration string (e.g., "PT1H30M")
      String isoDuration = "PT${picked.hour}H${picked.minute}M";
      String displayDuration = "${picked.hour}h ${picked.minute}m";
      updateDuration(isoDuration);
      controller.text = displayDuration;
    }
  }
}
