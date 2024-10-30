# Synclytic RingSnoozeWidget

**Synclytic RingSnoozeWidget** is an innovative Android app designed to help busy individuals take control of their Ring motion notifications with ease. In a world where time is of the essence, this app offers a quick and efficient way to snooze notifications from your Ring devices, allowing you to focus on what really matters. Whether you're at work, spending time with family, or simply enjoying a moment of peace, the RingSnoozeWidget helps you manage your notifications effectively.

## Purpose

The primary purpose of the Synclytic RingSnoozeWidget is to help busy people save precious seconds in their day. This app is perfect for those moments when you want to silence the incessant ringing of your Ring device, particularly on days when you are not expecting guests. With just a tap on a widget, you can snooze notifications without missing important alerts.

Imagine you’re at home, and the notifications from neighbors walking their dogs or the sound of garbage trucks driving by are overwhelming. The RingSnoozeWidget allows you to perform a **RAGE SNOOZE**, providing a moment of respite from unnecessary alerts while still keeping you informed of crucial notifications. By using this app, you gain control over your digital life, ensuring that only the notifications that matter reach you.

## Features

- **12 Hour Snooze Duration**: Perfect to set at the beginning of the day so that you can get your work done, and have the longest available snooze time set from Ring.

- **Easy to Use**: The app features a straightforward interface. Simply add a widget to your home screen, select your desired snooze duration, and tap it to activate. No complex settings or configurations are required.

- **No Need to Open the App**: One of the standout features of the RingSnoozeWidget is that you can snooze notifications without needing to open the Ring app itself. This saves time and keeps your focus on your current tasks.

## How It Works

The Synclytic RingSnoozeWidget utilizes Android's UI automation framework (UI Automator) to interact seamlessly with the Ring app. The widget is configured with a specific snooze duration, enabling a quick response when notifications become overwhelming. By tapping on the widget, users can trigger the corresponding snooze action in the Ring app, effectively silencing notifications for the selected time.

The integration of UI Automator means that the RingSnoozeWidget is not only user-friendly but also efficient in executing snooze commands. This functionality is vital for users who rely on their Ring devices for security but don’t want to be distracted by unnecessary notifications.

## Development

This project was developed as a final project for my **HarvardX CS50** class, where I chose to create an Android app primarily using **Java**. While minimal **Kotlin** is incorporated, the app is predominantly Java-based, reflecting my focus on mastering Java during the course. The codebase is designed to be modular and maintainable, ensuring that future enhancements can be integrated smoothly.

The development of the Synclytic RingSnoozeWidget was a learning journey, as I had to learn the hard way that Ring does not provide any API, and additionally, certain settings do not allow one to automatically open the app. So I had to add code to allow for a notification. It still saves a decent amount of time, but once there is an API allowed, this could become even easier and quicker.

## Future Enhancements

While the current version of the Synclytic RingSnoozeWidget is fully functional, I have exciting plans for future enhancements:

- **Customizable Snooze Durations**: I aim to allow users to set custom snooze durations beyond the preset options, catering to unique user preferences.

- **Integration with Ring API**: If the Ring API is ever released or if anyone successfully creates one, I plan to integrate it into the app. This integration could unlock new features and functionalities, making the app even more powerful.

- **Collaboration Opportunities**: If any developers have experience with the Ring API or are interested in collaborating on this project, The Devs Who Say Ni would love to hear from you! Together, we can expand the capabilities of the Synclytic RingSnoozeWidget.

## Contributing

Contributions to the Synclytic RingSnoozeWidget are always welcome! If you have ideas for improvements, bug fixes, or new features, please feel free to submit pull requests or report issues through the GitHub repository. Your contributions can help make this app even better for all users.

## License

This project is licensed under the **MIT License**. For details, please see the LICENSE file included in the repository.
