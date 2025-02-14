import 'package:flutter/material.dart';

// Done with the help of chatgpt

class BottomSheetSelector<T> extends StatelessWidget {
  final List<T> items;
  final List<T> selectedItems;
  final Function(List<T>) onSelectionChanged;
  final String title;
  final String buttonLabel;
  final String Function(T) itemLabel;
  final String textfield;

  const BottomSheetSelector({
    required this.items,
    required this.selectedItems,
    required this.onSelectionChanged,
    required this.title,
    required this.buttonLabel,
    required this.itemLabel,
    required this.textfield,
    super.key,
  });

  void showBottomSheet(BuildContext context) async {
    List<T> tempSelected = List.from(selectedItems);

    await showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (BuildContext context, StateSetter setStateInModal) {
            return Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const SizedBox(height: 10),
                Text(
                  title,
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const Divider(),
                Expanded(
                  child: ListView.builder(
                    shrinkWrap: true,
                    itemCount: items.length,
                    itemBuilder: (BuildContext context, int index) {
                      final item = items[index];
                      return CheckboxListTile(
                        title: Text(itemLabel(item)),
                        value: tempSelected.contains(item),
                        onChanged: (bool? isSelected) {
                          setStateInModal(() {
                            if (isSelected == true) {
                              tempSelected.add(item);
                            } else {
                              tempSelected.remove(item);
                            }
                          });
                        },
                      );
                    },
                  ),
                ),
                ElevatedButton(
                  onPressed: () {
                    Navigator.pop(context);
                    onSelectionChanged(tempSelected);
                  },
                  child: Text(buttonLabel),
                ),
                const SizedBox(height: 10),
              ],
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: 250,
          child: ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFF2F2F2),
              foregroundColor: Colors.black,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(6),
                side: const BorderSide(color: Colors.black, width: 1),
              ),
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            ),
            onPressed: () => showBottomSheet(context),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  textfield,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.normal,
                  ),
                ),
                const Icon(Icons.arrow_drop_down),
              ],
            ),
          ),
        ),
        const SizedBox(height: 5),
        Wrap(
          spacing: 8.0,
          runSpacing: 4.0,
          children: selectedItems
              .map((item) => Chip(
                    label: Text(itemLabel(item)),
                    onDeleted: () {
                      onSelectionChanged(selectedItems..remove(item));
                    },
                  ))
              .toList(),
        ),
      ],
    );
  }
}
