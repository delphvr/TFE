import 'package:calendar_app/utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class RoleElement extends StatefulWidget {
  final int projectId;
  final int userId;
  final String role;
  final VoidCallback onUpdate;

  const RoleElement({
    super.key,
    required this.projectId,
    required this.userId,
    required this.role,
    required this.onUpdate,
  });

  @override
  State<RoleElement> createState() => _RoleElementState();
}

class _RoleElementState extends State<RoleElement> {
  void _deleteRole() async {
    final String url = '${dotenv.env['API_BASE_URL']}/projects/${widget.projectId}/users/${widget.userId}/roles/${widget.role}';
    try {
      final response = await http.delete(Uri.parse(url));

      if (response.statusCode != 204) {
        Utils.errorMess('Erreur lors de la supression du role', 'Erreur lors de la suppression. Merci de réessayez plus tard.', context);
      }
    } catch (_) {}
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
          padding: widget.role == "Non défini"
              ? const EdgeInsets.symmetric(vertical: 15.0, horizontal: 10.0)
              : const EdgeInsets.symmetric(vertical: 1.0, horizontal: 10.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                widget.role,
                style: const TextStyle(
                  fontSize: 17,
                  color: Colors.black,
                ),
              ),
              if (widget.role != "Non défini")
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
