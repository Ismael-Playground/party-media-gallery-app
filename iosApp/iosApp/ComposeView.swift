import SwiftUI
import shared

/**
 * ComposeView - SwiftUI wrapper for Compose Multiplatform UI
 *
 * This UIViewControllerRepresentable bridges SwiftUI and Compose Multiplatform
 * by wrapping the ComposeUIViewController from the shared Kotlin module.
 *
 * Party Gallery - Dark Mode First Design
 * Uses the shared Compose UI instead of native SwiftUI
 */
struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // No updates needed - Compose handles its own state
    }
}

#if DEBUG
struct ComposeView_Previews: PreviewProvider {
    static var previews: some View {
        ComposeView()
            .ignoresSafeArea()
            .preferredColorScheme(.dark)
    }
}
#endif
