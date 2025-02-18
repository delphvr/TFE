import 'package:calendar_app/project/project_details.dart';
import 'package:calendar_app/utils.dart';
import 'package:flutter/material.dart';

class ProjectElement extends StatefulWidget {
  final int id;
  final String name;
  final String? description;
  final String? beginningDate;
  final String? endingDate;
  final VoidCallback onUpdate;

  const ProjectElement({
    super.key,
    required this.id,
    required this.name,
    this.description,
    this.beginningDate,
    this.endingDate,
    required this.onUpdate,
  });

  @override
  State<ProjectElement> createState() => _ProjectElementState();
}

class _ProjectElementState extends State<ProjectElement> {

  late String name;
  late String? description;
  late String? beginningDate;
  late String? endingDate;

  @override
  void initState() {
    super.initState();
    name = widget.name;
    description = widget.description;
    beginningDate = widget.beginningDate;
    endingDate = widget.endingDate;
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
      child: GestureDetector(
        onTap: () {
          Navigator.push(
            context, //MaterialPageRoute(builder: (context) => NewProjectPage())
            MaterialPageRoute(
              builder: (context) => ProjectDetailsPage(
                id: widget.id,
                name: name,
                description: description,
                beginningDate: beginningDate,
                endingDate: endingDate,
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
                Text(
                  name,
                  style: const TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: Colors.black,
                  ),
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
                        'Début: ${beginningDate != null ? Utils.formatDate(beginningDate) : "-"}',
                        style: const TextStyle(
                          fontSize: 14,
                          color: Colors.black54,
                        ),
                      ),
                      Text(
                        'Fin: ${endingDate != null ? Utils.formatDate(endingDate) : "-"}',
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
