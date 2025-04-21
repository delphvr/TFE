import 'package:calendar_app/organizer/project/project_details.dart';
import 'package:calendar_app/utils.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:convert';

/// Display all the information about the project with id [id].
/// Clickable widget, redirect to the information and gestion of the project. [refreshProjects] will be called when comming back from clicking on it.
/// If [organizerPage] is false and the user is an organizer on the project a small emodji is displayed.
class ProjectElement extends StatefulWidget {
  final int id;
  final bool organizerPage;
  final VoidCallback onUpdate;
  final FirebaseAuth? auth;
  final http.Client? client;

  const ProjectElement({
    super.key,
    required this.id,
    required this.organizerPage,
    required this.onUpdate,
    this.auth,
    this.client,
  });

  @override
  State<ProjectElement> createState() => _ProjectElementState();
}

class _ProjectElementState extends State<ProjectElement> {
  FirebaseAuth get auth => widget.auth ?? FirebaseAuth.instance;
  http.Client get client => widget.client ?? http.Client();
  late final User user;
  String? name;
  String? description;
  String? beginningDate;
  String? endingDate;
  bool isOrganizer = false;

  @override
  void initState() {
    super.initState();
    user = auth.currentUser!;
    getProjectData(context);
    checkIsOrganizer();
  }

  /// Update the variables [name], [description], [beginningDate] and [endingDate] with the data retreived from the backend for the project with id [widget.id].
  /// If an error occurs an error message is displayed.
  void getProjectData(BuildContext context) async {
    final String url = '${dotenv.env['API_BASE_URL']}/projects/${widget.id}';
    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final Map<String, dynamic> data =
            json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          name = data['name'];
          description = data['description'];
          beginningDate = data['beginningDate'];
          endingDate = data['endingDate'];
        });
      } else {
        if (context.mounted) {
          Utils.errorMess('Une erreur c\'est produite',
              'Merci de réessayer plus tard.', context);
        }
      }
    } catch (e) {
      if (context.mounted) {
        Utils.errorMess('Une erreur c\'est produite',
            'Merci de réessayer plus tard.', context);
      }
    }
  }

  /// Update the variable [isOrganizer] to true if the backend says that the user is an organizer on the project with id [widget.id], set to false otherwise.
  Future<void> checkIsOrganizer() async {
    final bool result = await getIsOrganizer(widget.id);
    setState(() {
      isOrganizer = result;
    });
  }

  /// Get if the user is an organizer on the project with id [widget.id].
  /// 
  /// [Returns]  true if the backend says that the user is an organizer on the project with id [widget.id], false otherwise.
  Future<bool> getIsOrganizer(int projectId) async {
    final email = user.email;
    final String url =
        '${dotenv.env['API_BASE_URL']}/projects/$projectId/is-organizer?email=$email';

    try {
      final response = await client.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final Map<String, dynamic> data =
            json.decode(utf8.decode(response.bodyBytes));
        return data['isOrganizer'] ?? false;
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
      child: GestureDetector(
        onTap: () {
          Navigator.push(
            context, 
            MaterialPageRoute(
              builder: (context) => ProjectDetailsPage(
                id: widget.id,
                organizerPage: widget.organizerPage,
              ),
            ),
          ).then((_) {
            widget.onUpdate();
          });
        },
        child: Container(
          decoration: BoxDecoration(
            color: const Color(0xFFF2F2F2),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(),
          ),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      name ?? '',
                      style: const TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: Colors.black,
                      ),
                    ),
                    if (isOrganizer && !widget.organizerPage)
                      const Icon(Icons.assignment),
                  ],
                ),
                const SizedBox(height: 10),
                if (description != null && description != "")
                  Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: Text(
                      'Description : $description',
                      style: const TextStyle(
                        fontSize: 16,
                        color: Colors.black,
                      ),
                    ),
                  ),
                if (beginningDate != null || endingDate != null)
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        'Début: ${beginningDate != null ? Utils.formatDateString(beginningDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 14,
                          color: Colors.black54,
                        ),
                      ),
                      Text(
                        'Fin: ${endingDate != null ? Utils.formatDateString(endingDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 14,
                          color: Colors.black54,
                        ),
                      ),
                    ],
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
