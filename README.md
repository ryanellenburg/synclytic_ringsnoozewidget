# Synclytic RingSnoozeWidget

This Android app provides a simple way to snooze your Ring notifications directly from your home screen using a collection of convenient widgets.

## Features

*   Multiple Snooze Durations: Choose from 7 different snooze times: 30 minutes, 1 hour, 2 hours, 3 hours, 4 hours, 8 hours, and 12 hours.
*   Easy to Use: Simply add a widget to your home screen and tap it to snooze Ring notifications for the selected duration.
*   No Need to Open the App: Snooze Ring notifications without even opening the Ring app.

## How it Works

The app utilizes Android's UI automation framework (UI Automator) to interact with the Ring app and perform the snooze action. Each widget is configured with a specific snooze duration, and tapping on the widget triggers the corresponding snooze action in the Ring app.

## Usage

1.  Add a RingSnoozeWidget to your home screen.
2.  Select the desired snooze duration from the list of available widgets.
3.  Tap on the widget to snooze Ring notifications for the chosen duration.

## Development

The app is built with Kotlin and Java, utilizing Android's AppWidgetProvider and UI Automator framework.

## Future Enhancements

*   Customizable snooze durations
*   Integration with Ring API (if they will ever allow that to happen)
*   Integration with some open source Ring API here on GitHub (once I become a better developer and figure all this out)

## Contributing

Contributions are welcome! Feel free to submit pull requests or report issues.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
