export class ReportRequest{

  constructor(cardId: number, message: string) {
    this.card = cardId;
    this.message = message;
  }

  card: number;
  message: string;
}
