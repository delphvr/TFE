import 'package:calendar_app/organizer/project/project_admin.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

void main() {
  group('admin project list', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('project admin page informations check', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ProjectOrganizerPage(
          client: client,
          auth: mockAuth,
        ),
      ));

      await tester.pumpAndSettle();

      expect(find.byType(ProjectOrganizerPage), findsOneWidget);

      expect(find.text("Nouveau projet"), findsOneWidget);
      await tester.tap(find.text('Projets Archivés'));
      await tester.pumpAndSettle();
      //debugDumpApp();
      expect(find.text("Description : Spectacle de danse et de chant"), findsOneWidget);
      expect(find.text("Aladin"), findsOneWidget);
      expect(find.text("Un project de danse"), findsOneWidget);
      expect(find.text("Début: 01-04-2024"), findsOneWidget);
      expect(find.text("Fin: 10-04-2024"), findsOneWidget);
    });
  });
}
