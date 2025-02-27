import 'package:calendar_app/utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;

class RoleOrParticipantElement extends StatefulWidget {
  final int? projectId;
  final int? userId;
  final String name;
  final VoidCallback? onUpdate;

  const RoleOrParticipantElement({
    super.key,
    this.projectId,
    this.userId,
    required this.name,
    this.onUpdate,
  });

  @override
  State<RoleOrParticipantElement> createState() =>
      _RoleOrParticipantElementState();
}

class _RoleOrParticipantElementState extends State<RoleOrParticipantElement> {
  void _deleteRole() async {
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}/roles/${widget.name == "Organisateur" ? "Organizer" : widget.name}';
    try {
      final response = await http.delete(Uri.parse(url));
      if (response.statusCode == 400) {
        if (mounted) {
          Utils.errorMess(
              'Erreur lors de la supression du role',
              'Il doit rester au moins un organisateur sur le projet.',
              context);
        }
      } else if (response.statusCode != 204) {
        if (mounted) {
          Utils.errorMess(
              'Erreur lors de la supression du role',
              'Erreur lors de la suppression. Merci de réessayez plus tard.',
              context);
        }
      }
    } catch (_) {}
    widget.onUpdate!();
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
          padding: widget.name == "Non défini" || widget.onUpdate == null
              ? const EdgeInsets.symmetric(vertical: 15.0, horizontal: 10.0)
              : const EdgeInsets.symmetric(vertical: 1.0, horizontal: 10.0),
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
              if (widget.name != "Non défini" && widget.onUpdate != null)
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: _deleteRole,
                ),
            ],
          ),
        ),
      ),
    );
  }
}
