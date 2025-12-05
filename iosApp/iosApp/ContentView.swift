import SwiftUI

/**
 * Party Gallery iOS App
 *
 * ContentView - Main entry point that wraps Compose Multiplatform UI
 *
 * This view delegates all UI rendering to Compose Multiplatform
 * via ComposeView (UIViewControllerRepresentable wrapper).
 *
 * Design: Dark Mode First
 * - Background: #0A0A0A
 * - Primary: Amber #F59E0B
 *
 * Original SwiftUI implementation backed up at:
 * ContentView.swift.swiftui.backup
 */
struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

#if DEBUG
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
            .preferredColorScheme(.dark)
    }
}
#endif
