import 'package:calendar_app/utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;

/// Display the name of the rehearsal, plus a clickable cross that will delete the precedence relation between the rehearsal [current] and [previous].
/// Either [current] or [previous] should be equal to [rehearsalId].
class PrecedenceRehearsalElement extends StatefulWidget {
  final int rehearsalId;
  final String name;
  final int current;
  final int previous;
  final VoidCallback onUpdate;

  const PrecedenceRehearsalElement({
    super.key,
    required this.rehearsalId,
    required this.name,
    required this.current,
    required this.previous,
    required this.onUpdate,
  });

  @override
  State<PrecedenceRehearsalElement> createState() =>
      _PrecedenceRehearsalElementState();
}

class _PrecedenceRehearsalElementState
    extends State<PrecedenceRehearsalElement> {

  final String errorTitle = 'Erreur lors de la suppression de la relation de précédence';

  /// Delete the sequence constraint between the rehearsal with id [widget.current] and the rehearsal with id [widget.previous].
  /// If an error occurs an error message will be display.
  void deletePrecedence() async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/rehearsals/precedences?current=${widget.current}&previous=${widget.previous}';

    try {
      final response = await http.delete(Uri.parse(url));

      if (response.statusCode != 204) {
        if (mounted) {
          Utils.errorMess(
              errorTitle,
              'Merci de réessayer plus tard.',
              context);
        }
      }
    } catch (_) {
      if (mounted) {
        Utils.errorMess(
            errorTitle,
            'Merci de réessayer plus tard.',
            context);
      }
    }
    widget.onUpdate();
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2.0, horizontal: 30.0),
      child: Container(
        decoration: BoxDecoration(
          color: const Color(0xFFF2F2F2),
          borderRadius: BorderRadius.circular(8),
          border: Border.all(),
        ),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 5.0, horizontal: 15.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                widget.name,
                style: const TextStyle(
                  fontSize: 17,
                  color: Colors.black,
                ),
              ),
              IconButton(
                icon: const Icon(Icons.close),
                onPressed: deletePrecedence,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
