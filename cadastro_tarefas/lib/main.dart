import 'package:cadastrotarefas/pages/filtro_page.dart';
import 'package:cadastrotarefas/pages/lista_tarefas_page.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(CadastroTarefasApp());
}

class CadastroTarefasApp extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Cadastro de Tarefas',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: ListaTarefasPage(),
      routes: {
        FiltroPage.ROUTE_NAME: (_) => FiltroPage(),
      },
    );
  }

}