import 'package:calendar_app/project/project_user.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';

import 'mocks_test.dart';
import 'mocks_test.mocks.dart';

void main() {
  group('user projects', () {
    late MockClient client;
    late MockFirebaseAuth mockAuth;
    late MockUserCredential mockUserCredential;

    setUp(() async {
      client = MockClient();
      mockAuth = MockFirebaseAuth();
      mockUserCredential = MockUserCredential();

      await setupCommonMocks(client, mockAuth, mockUserCredential);
    });

    testWidgets('rehearsal list', (WidgetTester tester) async {
      await tester.pumpWidget(MaterialApp(
        home: ProjectsUserPage(
          client: client,
          auth: mockAuth,
        ),
      ));
      await tester.pumpAndSettle();
      expect(find.byType(ProjectsUserPage), findsOneWidget);
      await tester.tap(find.text('Projets Archivés'));
      await tester.pumpAndSettle();
      expect(find.text("Un project de danse"), findsOneWidget);
      expect(find.text("Description : Danse danse danse..."), findsOneWidget);
      expect(find.text("Début : 05-05-2024"), findsOneWidget);
      expect(find.text("Fin : 10-05-2024"), findsOneWidget);
    });

  });
}
