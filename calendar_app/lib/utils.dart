import 'package:flutter/material.dart';

class Utils {

  /// Shows an error dialog with a given [title] and [message].
  /// Displays a `AlertDialog` with an "OK" button to closse the dialog.
  ///
  /// - [title] The title of the dialog
  /// - [message] The content/message of the dialog
  /// - [context] The BuildContext in which to show the dialog
  static void errorMess(String title, String message, BuildContext context) {
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

  /// Format a string from DD-MM-YY to YYYY-MM-DD and visversa.
  ///
  /// - [date] the string to be reverted
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

  /// Convert a DateTime to a sting YYYY-MM-DD.
  /// If the day or month is one digit a 0 will be added in front e.g 2025-04-13 and not 2025-4-13.
  ///
  /// - [date] the DateTime to be converted into a string
  static String formatDateTime(DateTime? date) {
    if (date == null) {
      return "";
    }
    return "${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}";
  }

  /// Validates whether the given [email] string is in a valid email format.
  ///
  /// - [email] the string to bechecked
  /// - [Returns] `true` if the email format is valid. and `false` otherwise.
  //Source: https://stackoverflow.com/questions/16800540/how-should-i-check-if-the-input-is-an-email-address-in-flutter
  static bool isValidEmail(String email) {
    return RegExp(
            r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$')
        .hasMatch(email);
  }

  /// Displays a confirmation dialog with a title, message, and two action buttons.
  /// The dialog shows an "annuler" (cancel) button and a "oui" (yes) button. 
  /// Tapping "annuler" will close the dialog.
  /// Tapping "oui" will close the dialog and then execute the given [act] function.
  ///
  /// - [title] The title of the dialog
  /// - [message] The content/message of the dialog
  /// - [act] The function to execute if the user press on "oui"
  /// - [context] The BuildContext in which to show the dialog
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

  /// Parses an ISO 8601 duration string (e.g., `PT2H30M`) into a `Duration` object.
  ///
  /// - [duration] the string to be parse
  /// - [Return] the corresponding Duration object
  static Duration parseDuration(String duration) {
    final regex = RegExp(r'PT(?:(\d+)H)?(?:(\d+)M)?');
    final match = regex.firstMatch(duration);
    int hours = 0;
    int minutes = 0;
    if (match != null) {
      if (match.group(1) != null) {
        hours = int.parse(match.group(1)!);
      }
      if (match.group(2) != null) {
        minutes = int.parse(match.group(2)!);
      }
    }
    return Duration(hours: hours, minutes: minutes);
  }

  /// Parses an ISO 8601 duration string (e.g., `PT2H30M`) into a String in the format XhXm
  /// Ff there is no hours the Xm, if there is no minutes then just Xh, otherwise XhXm
  ///
  /// - [duration] the string to be parse
  /// - [Return] the corresponding String
  static String formatDuration(String duration) {
    final regex = RegExp(r'PT(?:(\d+)H)?(?:(\d+)M)?');
    final match = regex.firstMatch(duration);

    if (match == null) return duration;

    final hours = match.group(1);
    final minutes = match.group(2);

    String formatted = '';
    if (hours != null) formatted += '${hours}h';
    if (minutes != null) formatted += minutes;
    if (hours == null && minutes != null) formatted = '${minutes}m';

    return formatted;
  }

  /// Displays a date picker dialog and call [onDateSelected] with the selected date.
  /// The selected date is formatted as `dd-MM-yyyy` and passed to the given [controller].
  ///
  /// [context] BuildContext used to show the date picker
  /// [controller] TextEditingController to update with the selected date
  /// [selectedDate] The initially selected date. Defaults to today if null
  /// [onDateSelected] Callback executed with the selected DateTime
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

  /// Displays a time picker dialog and updates the provided [controller] with the selected duration in the format of XhX.
  /// Calls [updateDuration] with the selected diration formated as an ISO 8601 duration string.
  ///
  /// - [context] BuildContext used to show the time picker
  /// - [controller] TextEditingController to update with the selected duration
  /// - [selectedDuration] The initially selected duration in ISO 8601 format. If null, defaults to 2 hours
  /// - [updateDuration] Callback executed with the selected duration
  static Future<void> selectDuration(
      BuildContext context,
      TextEditingController controller,
      String? selectedDuration,
      Function(String) updateDuration) async {
    int initialHours = 2;
    int initialMinutes = 0;

    if (selectedDuration != null) {
      selectedDuration = formatDuration(selectedDuration);
    }
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: TimeOfDay(hour: initialHours, minute: initialMinutes),
    );
    if (picked != null) {
      String isoDuration = "PT${picked.hour}H${picked.minute}M";
      String displayDuration = "${picked.hour}h${picked.minute}";
      updateDuration(isoDuration);
      controller.text = displayDuration;
    }
  }

  /// Displays a time picker dialog and calls [onTimeSelected] with the selected time.
  /// The selected time is formatted and passed to the given [controller].
  ///
  /// - [context] BuildContext used to show the time picker
  /// - [controller] TextEditingController to update with the selected time
  /// - [selectedTime] The initially selected time. Defaults to the current time if null
  /// - [onTimeSelected] Callback executed with the selected TimeOfDay
  static Future<void> selectTime(
      BuildContext context,
      TextEditingController controller,
      TimeOfDay? selectedTime,
      Function(TimeOfDay) onTimeSelected) async {
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedTime ?? TimeOfDay.now(),
    );

    if (picked != null) {
      onTimeSelected(picked);
      if (context.mounted) {
        controller.text = picked.format(context);
      }
    }
  }

  /// Formats a string representing a time in `HH:mm:ss` format to `HH:mm` format.
  /// 
  /// - [time] The time string to format, which should be in `HH:mm:ss` format or `HH:mm` format
  /// - [Returns] the formatted time or `null` if the input was `null`
  static String? formatTimeString(String? time) {
    if (time != null) {
      List<String> parts = time.split(':');
      if (parts.length == 3) {
        return "${parts[0]}:${parts[1]}";
      }
      return time;
    }
    return time;
  }

  /// Parses a time string in `HH:mm` format into a `TimeOfDay` object.
  ///
  /// - [timeString] The time string to be parsed
  /// - [Returns] the corresponding `TimeOfDay` 
  static TimeOfDay parseTimeOfDay(String timeString) {
    final parts = timeString.split(":");
    final hour = int.parse(parts[0]);
    final minute = int.parse(parts[1]);
    return TimeOfDay(hour: hour, minute: minute);
  }
}
