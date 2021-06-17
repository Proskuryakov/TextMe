import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-update-delete-message',
  templateUrl: './update-delete-message.component.html',
  styleUrls: ['./update-delete-message.component.sass']
})
export class UpdateDeleteMessageComponent implements OnInit {

  @Input() modalId = '';
  @Output() onDeleteMessage = new EventEmitter<void>();
  @Output() onUpdateMessage = new EventEmitter<string>();
  @Input() text = '';

  constructor() { }

  ngOnInit(): void {
  }

  deleteMessage(): void {
    this.onDeleteMessage.emit();
  }

  updateMessage(): void {
    this.onUpdateMessage.emit(this.text);
  }

}
