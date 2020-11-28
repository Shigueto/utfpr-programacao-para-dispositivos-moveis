import 'package:intl/intl.dart';

class Tarefa {

  static const CAMPO_ID = '_id';
  static const CAMPO_DESCRICAO = 'descricao';
  static const CAMPO_PRAZO = 'prazo';

  int id;
  String descricao;
  DateTime prazo;

  Tarefa({this.id, this.descricao, this.prazo});

  String get prazoFormatado {
    if (prazo == null) {
      return '';
    }
    return DateFormat('dd/MM/yyyy').format(prazo);
  }

}